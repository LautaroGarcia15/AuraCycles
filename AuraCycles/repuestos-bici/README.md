# Sistema de Reseñas y Devoluciones — Repuestos de Bicicleta

TP Integrador · Desarrollo de Aplicaciones II · UADE

Arquitectura en capas sobre Jakarta EE 10, según el diagrama de capas del grupo.

| Capa | Tecnología | Paquete |
|---|---|---|
| Presentación | JSF (Jakarta Faces 4.0) + CDI | `presentacion/` + `webapp/*.xhtml` |
| Negocio | EJB (Jakarta Enterprise Beans) | `negocio/` |
| Datos | JPA / Hibernate + MySQL | `datos/` + `modelo/` |

---

## 1. Qué hace falta instalar

| Herramienta | Versión | Nota |
|---|---|---|
| JDK | 17 o 21 | El proyecto compila con `release 17` |
| WildFly | variante **EE 10** | Desde WildFly 40 el estándar pasó a EE 11 y eliminó `@ManagedBean`. La variante EE 10 evita sorpresas |
| MySQL | 8.x | Más MySQL Workbench para correr el schema |
| Eclipse | IDE **for Enterprise Java and Web Developers** | La edición "Java Developers" pelada no trae herramientas de servidor |
| JBoss Tools | desde el Eclipse Marketplace | Arranca y para WildFly desde el IDE |

Maven ya viene embebido en Eclipse.

---

## 2. Crear la base

Abrí `sql/schema.sql` en MySQL Workbench y ejecutalo entero. Crea el esquema
y carga datos de prueba: 8 repuestos, 5 usuarios, 8 compras, 4 reseñas y
1 devolución pendiente.

**Todos los usuarios tienen la contraseña `Clave1234`:**

| Email | Rol |
|---|---|
| `ana@mail.com` | CLIENTE |
| `carlos@mail.com` | CLIENTE |
| `lucia@mail.com` | CLIENTE |
| `javier@mail.com` | CLIENTE |
| `mod@mail.com` | **MODERADOR** |

---

## 3. Conectar MySQL a WildFly

Esto es lo que le come la tarde a todo el mundo, así que seguilo tal cual.

Bajá el `.jar` del driver `mysql-connector-j`. Levantá WildFly en una terminal:

```bash
cd WILDFLY_HOME/bin
./standalone.sh
```

En **otra** terminal:

```bash
cd WILDFLY_HOME/bin
./jboss-cli.sh --connect
```

Y adentro del CLI, en este orden:

```
module add --name=com.mysql --resources=/ruta/al/mysql-connector-j-9.x.x.jar --dependencies=java.sql

/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)

data-source add --name=RepuestosDS --jndi-name=java:/RepuestosDS --driver-name=mysql --connection-url=jdbc:mysql://localhost:3306/repuestos_bici?serverTimezone=America/Argentina/Buenos_Aires --user-name=root --password=TU_PASSWORD --use-ccm=true --min-pool-size=5 --max-pool-size=20

data-source test-connection-in-pool --name=RepuestosDS
```

El último comando tiene que devolver `true`. Si devuelve error, el problema
está en el usuario, la contraseña o en que MySQL no está levantado — no en
la aplicación.

El nombre JNDI `java:/RepuestosDS` es el mismo que figura en
`persistence.xml`. Si lo cambiás, cambialo en los dos lados.

---

## 4. Configurar la seguridad declarativa

La seguridad tiene tres piezas y las tres hacen falta:

1. **Autenticación en el contenedor.** `LoginBean` valida el formulario y
   llama a `request.login()`. Ahí WildFly (Elytron) verifica la contraseña
   contra la tabla `usuario` y carga el rol. Sin este paso el contenedor
   atiende a todos como anónimos y `@RolesAllowed` rechaza hasta al moderador.
2. **Autorización web.** `web.xml` restringe `moderacion.xhtml` al rol
   MODERADOR y el resto de las pantallas a usuarios logueados. Un CLIENTE que
   escribe la URL del panel recibe 403.
3. **Autorización en negocio.** `@RolesAllowed("MODERADOR")` en los 6 métodos
   de `ServicioDeModeracion`.

En el CLI:

```
/subsystem=elytron/jdbc-realm=repuestos-realm:add(principal-query=[{sql="SELECT SUBSTRING_INDEX(password_hash, ':', -1), SUBSTRING_INDEX(password_hash, ':', 1), rol FROM usuario WHERE email = ?",data-source=RepuestosDS,salted-simple-digest-mapper={algorithm=salt-password-digest-sha-256,password-index=1,salt-index=2},attribute-mapping=[{index=3,to=Roles}]}])

/subsystem=elytron/security-domain=repuestos-domain:add(default-realm=repuestos-realm,permission-mapper=default-permission-mapper,realms=[{realm=repuestos-realm}])

/subsystem=undertow/application-security-domain=repuestos-domain:add(security-domain=repuestos-domain)

/subsystem=ejb3/application-security-domain=repuestos-domain:add(security-domain=repuestos-domain)

reload
```

**Si ya habías creado el realm con la versión anterior** (la del
`clear-password-mapper`), no lo borres: reemplazá solo la consulta.

```
/subsystem=elytron/jdbc-realm=repuestos-realm:write-attribute(name=principal-query,value=[{sql="SELECT SUBSTRING_INDEX(password_hash, ':', -1), SUBSTRING_INDEX(password_hash, ':', 1), rol FROM usuario WHERE email = ?",data-source=RepuestosDS,salted-simple-digest-mapper={algorithm=salt-password-digest-sha-256,password-index=1,salt-index=2},attribute-mapping=[{index=3,to=Roles}]}])

reload
```

> **Para la defensa oral.** `PasswordHash` guarda `salt:hash` en una sola
> columna. La consulta la parte en dos con `SUBSTRING_INDEX` y Elytron
> recalcula SHA-256 sobre salt + contraseña (`salt-password-digest-sha-256`,
> el mismo orden que usa `PasswordHash`). Así la aplicación y el contenedor
> validan exactamente el mismo hash, sin contraseñas en claro.
> En producción se usaría BCrypt o Argon2, que son lentos a propósito.

Si el login da "El servidor rechazó el ingreso", el problema está en el realm,
no en el código: revisá la consulta y hacé `reload`.

---

## 5. Importar en Eclipse

1. `File → Import → Maven → Existing Maven Projects`
2. Elegí la carpeta `repuestos-bici`
3. `Window → Show View → Servers → New Server → JBoss Community → WildFly`
4. Apuntá al directorio de WildFly y al JDK
5. Botón derecho sobre el servidor → `Add and Remove` → agregá `repuestos-bici`
6. `Start`

O por línea de comandos:

```bash
mvn clean package
cp target/repuestos.war WILDFLY_HOME/standalone/deployments/
```

La app queda en **http://localhost:8080/repuestos/**

---

## 6. Recorrido para la demo

Este es el orden que conviene mostrar en la defensa, porque recorre el caso
de uso resaltado del diagrama de punta a punta. Antes de empezar, corré de
nuevo `sql/schema.sql` para arrancar con los datos limpios.

1. Entrá como `ana@mail.com`
2. **Productos** → abrí "Cubierta MTB 29 x 2.10" → mirá las reseñas aprobadas
3. **Mis compras** → "Solicitar devolución" en la cubierta
4. Elegí **"Me arrepentí de la compra"**, escribí un comentario, enviá → sale el código `DEV-000002`.
   En la consola aparece `[CICLO DE VIDA] @PostConstruct`, `@Remove` y `@PreDestroy` del componente stateful
5. Cerrá sesión, entrá como `mod@mail.com`
6. **Moderación** → solapa Devoluciones. Hay dos: `DEV-000001` (precargada, de Javier) y `DEV-000002` (la de Ana). Aprobá la de Ana
7. Mirá la consola de WildFly: el reembolso es de **$42.120** (se retiene el 10 % por arrepentimiento, patrón Strategy),
   aparecen los avisos de `ServicioDeNotificaciones` y el stock de la cubierta volvió a subir
8. Cerrá sesión, entrá como `carlos@mail.com` y escribí `/repuestos/moderacion.xhtml` en la barra: **403**

Para mostrar el pool del stateless: con el moderador, cambiá varias veces de
solapa. `@PostConstruct` de `ServicioDeModeracion` aparece una sola vez aunque
se hagan muchas llamadas.

---

## 7. Cómo se mapea contra el enunciado

**Entrega Obligatoria N.º 1**

| Requisito | Dónde está |
|---|---|
| Al menos 3 componentes implementados y desplegados, en capas | Los 8 servicios. Para la demo: Usuarios, Devoluciones y Moderación |
| 1 stateful y 1 stateless, con ciclo de vida gestionado por el contenedor | `ServicioDeDevoluciones` (`@PostConstruct`, `@Remove`, `@PreDestroy`); `ServicioDeModeracion` (`@PostConstruct`, `@PreDestroy`). Logs `[CICLO DE VIDA]` |
| Al menos 3 patrones de diseño | DAO (repositorios), Facade (`ServicioDeModeracion`), Strategy (`PoliticaDeReembolso` y sus dos implementaciones) |
| Seguridad declarativa: autenticación y autorización por rol | Elytron + `request.login()`; `security-constraint` en `web.xml`; `@RolesAllowed` en 6 métodos |
| Documento técnico de 5 a 8 páginas | PDF de la entrega |

**Adelantado de la Entrega Obligatoria N.º 2:** transacciones declarativas en
`aprobarDevolucion()` (6 pasos en una transacción, rollback ante
`ReglaNegocioException`).

**Pendiente para la Entrega Obligatoria N.º 2:** SOAP con WSDL, REST,
mensajería con cola y tópico. `ServicioDeNotificaciones` ya está escrito para
convertirse en el publicador del tópico sin cambiar ninguna firma.

**Fuera del checklist:** el diseño responsive y el tema oscuro son mejoras de
interfaz. No figuran como requisito ni como punto extra del enunciado.

---

## 8. Las preguntas que te van a hacer

**¿Por qué `ServicioDeDevoluciones` es stateful y el resto no?**
Porque pedir una devolución es un asistente de tres pasos: elegís la compra,
cargás el motivo, confirmás. Entre paso y paso hay un borrador que tiene que
sobrevivir. Si fuera stateless habría que guardarlo en la sesión HTTP, que es
meter estado de negocio en la capa de presentación — el anti-patrón de la
diapositiva 36. Los otros siete resuelven cada operación en una sola llamada.
En la consola se ve la diferencia: el stateful nace y muere con cada
solicitud; el stateless se crea una vez y el pool lo reutiliza.

**¿Dónde está el Strategy?**
En `negocio/reembolso/`. `PoliticaDeReembolso` es la interfaz,
`ReembolsoTotal` y `ReembolsoConRetencion` son las estrategias, y
`ServicioDeReembolsos` elige la que aplica al motivo. Sumar una política nueva
es agregar una clase, sin tocar el servicio.

**¿Qué pasa si un cliente intenta moderar?**
Por URL, el contenedor le devuelve 403 (`web.xml`). Si alguien saltea la capa
web, `@RolesAllowed` corta la llamada al EJB con `EJBAccessException`. En
ningún lado hay un `if (esModerador)` escrito a mano.

**¿Por qué Moderación, Reembolsos y Notificaciones no tienen repositorio?**
Moderación opera sobre entidades ajenas y usa `ResenaRepository` y
`DevolucionRepository`. El monto del reembolso se guarda en la fila de
`devolucion` porque un reembolso no existe fuera de la devolución que lo
origina. Notificaciones no persiste nada, solo emite avisos. No creamos
repositorios para clases que no tienen entidad propia.

**¿Dónde está la regla de negocio?**
En `negocio/`, nunca en las pantallas. `DevolucionBean` no valida plazos ni
duplicados: llama a `ServicioDeDevoluciones.seleccionarCompra()` y ese método
decide. Comparalo con `ProductosBean.comprar()`, que tampoco chequea stock:
lo hace `ServicioDeVentas` dentro de una transacción con bloqueo pesimista.
