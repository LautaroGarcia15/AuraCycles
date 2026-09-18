-- Sistema de Resenas y Devoluciones - Repuestos de Bicicleta
-- Ejecutar completo antes del primer despliegue. Hibernate no genera el esquema.

DROP DATABASE IF EXISTS repuestos_bici;
CREATE DATABASE repuestos_bici CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE repuestos_bici;

-- ---------- USUARIO ----------
CREATE TABLE usuario (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  nombre        VARCHAR(120) NOT NULL,
  rol           VARCHAR(20)  NOT NULL
) ENGINE=InnoDB;

-- ---------- PRODUCTO ----------
CREATE TABLE producto (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  codigo      VARCHAR(40)  NOT NULL UNIQUE,
  nombre      VARCHAR(160) NOT NULL,
  categoria   VARCHAR(60),
  descripcion VARCHAR(500),
  precio      DECIMAL(12,2) NOT NULL,
  stock       INT NOT NULL DEFAULT 0,
  INDEX idx_producto_categoria (categoria)
) ENGINE=InnoDB;

-- ---------- VENTA ----------
CREATE TABLE venta (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id  BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad    INT NOT NULL,
  importe     DECIMAL(12,2) NOT NULL,
  fecha       DATETIME NOT NULL,
  CONSTRAINT fk_venta_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuario(id),
  CONSTRAINT fk_venta_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
  INDEX idx_venta_usuario (usuario_id)
) ENGINE=InnoDB;

-- ---------- RESENA ----------
CREATE TABLE resena (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id     BIGINT NOT NULL,
  producto_id    BIGINT NOT NULL,
  puntaje        INT NOT NULL,
  titulo         VARCHAR(160),
  texto          TEXT,
  estado         VARCHAR(20) NOT NULL,
  fecha          DATETIME NOT NULL,
  motivo_rechazo VARCHAR(200),
  CONSTRAINT fk_resena_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuario(id),
  CONSTRAINT fk_resena_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
  CONSTRAINT ck_resena_puntaje  CHECK (puntaje BETWEEN 1 AND 5),
  INDEX idx_resena_estado (estado),
  INDEX idx_resena_producto (producto_id)
) ENGINE=InnoDB;

-- ---------- DEVOLUCION ----------
-- El monto del reembolso vive aca: no existe fuera de la devolucion que lo origina.
CREATE TABLE devolucion (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  venta_id        BIGINT NOT NULL,
  motivo          VARCHAR(40) NOT NULL,
  comentario      TEXT,
  estado          VARCHAR(20) NOT NULL,
  monto_reembolso DECIMAL(12,2),
  fecha_solicitud DATETIME NOT NULL,
  fecha_resolucion DATETIME,
  CONSTRAINT fk_devolucion_venta FOREIGN KEY (venta_id) REFERENCES venta(id),
  CONSTRAINT uq_devolucion_venta UNIQUE (venta_id),
  INDEX idx_devolucion_estado (estado)
) ENGINE=InnoDB;


-- Datos de prueba. Todos los usuarios tienen la contrasena Clave1234;
-- los hashes se generaron con GeneradorHash, cada uno con su propio salt.

INSERT INTO usuario (email, password_hash, nombre, rol) VALUES
('ana@mail.com',    'LbXeJJyPwq0KuE0kHzDnfw==:lwE9/IYKe2vZglC2y0YVdLwRBJTLhohBWoK7iu4b320=', 'Ana Torres',      'CLIENTE'),
('carlos@mail.com', 'otOtU0dwI9Ng9lEHFamnRQ==:ePW0LG68Si/jzCjf9Rqps9dZPjPqcdiBoBWtCZspa54=', 'Carlos Duarte',   'CLIENTE'),
('lucia@mail.com',  'U0nDdfxt/ff9WXFRM9+b6g==:dfLTdL3fBeDJt9BJENQz/VxJc49xGa27jW3UNInVIgQ=', 'Lucia Ferreyra',  'CLIENTE'),
('javier@mail.com', 'm4arGOOpZ9XTMcA0Rww8Og==:z/gcin7CWcx7dVCGR7CDRnxJg+ajTQ/nZsdeLzfpZDA=', 'Javier Pereyra',  'CLIENTE'),
('mod@mail.com',    'y+KNs4Z53pzYiI3uQ+FBVg==:x/d/y5Ga6rkfSw5g1qXxIz7jT/EJRsvZwrUDMiCqTgg=', 'Marina Avalos',   'MODERADOR');

INSERT INTO producto (codigo, nombre, categoria, descripcion, precio, stock) VALUES
('CUB-29-210', 'Cubierta MTB 29 x 2.10',        'Cubiertas',
 'Cubierta rodado 29 con taco medio para uso mixto, tanto en tierra compactada como en asfalto.', 46800.00, 14),

('CAS-1134-9', 'Cassette 11-34T 9 velocidades', 'Transmision',
 'Cassette de 9 velocidades con desarrollo amplio, ideal para subidas exigentes.', 58700.00, 7),

('CAD-HG53-9', 'Cadena Shimano HG-53 9v',       'Transmision',
 'Cadena de 9 velocidades con tratamiento antioxido. Incluye pin de union.', 23400.00, 22),

('PAS-DISC-01','Pastillas de freno a disco',    'Frenos',
 'Juego de pastillas organicas para freno a disco. Buena mordida en humedo.', 9200.00, 40),

('DES-M370',   'Desviador trasero Altus M370',  'Transmision',
 'Desviador trasero de 9 velocidades, jaula media. Cambios precisos y buen desarrollo.', 71500.00, 5),

('CAM-29-PRE', 'Camara 29 valvula presta',      'Cubiertas',
 'Camara de aire rodado 29 con valvula presta de 48 mm.', 7800.00, 55),

('MAZ-DEL-32', 'Maza delantera 32 rayos',       'Ruedas',
 'Maza delantera de aluminio, 32 rayos, eje solido de 9 mm.', 34900.00, 9),

('SIL-COMF-1', 'Sillin comfort con gel',        'Contacto',
 'Sillin ancho con relleno de gel, pensado para uso urbano y paseos largos.', 18600.00, 18);

-- Compras: mezcla de recientes (devolvibles) y viejas (fuera de los 30 dias)
INSERT INTO venta (usuario_id, producto_id, cantidad, importe, fecha) VALUES
(1, 1, 1,  46800.00, DATE_SUB(NOW(), INTERVAL  4 DAY)),   -- Ana, cubierta       -> devolvible
(1, 4, 2,  18400.00, DATE_SUB(NOW(), INTERVAL 12 DAY)),   -- Ana, pastillas      -> devolvible
(2, 3, 1,  23400.00, DATE_SUB(NOW(), INTERVAL  6 DAY)),   -- Carlos, cadena      -> devolvible
(2, 5, 1,  71500.00, DATE_SUB(NOW(), INTERVAL 45 DAY)),   -- Carlos, desviador   -> VENCIDO
(3, 2, 1,  58700.00, DATE_SUB(NOW(), INTERVAL  9 DAY)),   -- Lucia, cassette     -> devolvible
(3, 6, 3,  23400.00, DATE_SUB(NOW(), INTERVAL  2 DAY)),   -- Lucia, camaras      -> devolvible
(4, 5, 1,  71500.00, DATE_SUB(NOW(), INTERVAL 15 DAY)),   -- Javier, desviador   -> devolvible
(4, 8, 1,  18600.00, DATE_SUB(NOW(), INTERVAL 20 DAY));   -- Javier, sillin      -> devolvible

-- Dos resenas aprobadas y dos pendientes, para ver las dos pantallas.
INSERT INTO resena (usuario_id, producto_id, puntaje, titulo, texto, estado, fecha) VALUES
(1, 1, 5, 'Excelente agarre',
 'La use en tierra y en asfalto. En mojado responde mucho mejor de lo que esperaba.',
 'APROBADA', DATE_SUB(NOW(), INTERVAL 3 DAY)),

(3, 2, 4, 'Buen desarrollo',
 'Cambios precisos, buen desarrollo para subidas. El montaje me llevo un rato.',
 'APROBADA', DATE_SUB(NOW(), INTERVAL 7 DAY)),

(2, 3, 4, 'Cumple sin problemas',
 'Cadena firme, el pin de union viene incluido y entro sin herramienta especial.',
 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 1 DAY)),

(4, 8, 2, 'Mas duro de lo que esperaba',
 'El gel se siente poco. Para salidas de mas de una hora me resulto incomodo.',
 'PENDIENTE', NOW());

-- Una devolucion pendiente, para el panel de moderacion.
INSERT INTO devolucion (venta_id, motivo, comentario, estado, fecha_solicitud) VALUES
(7, 'NO_COMPATIBLE',
 'Lo pedi para una bici de 8 velocidades y es de 9. No entra en el patin.',
 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 1 DAY));


-- Verificacion
SELECT 'usuarios'    AS tabla, COUNT(*) AS filas FROM usuario
UNION ALL SELECT 'productos',    COUNT(*) FROM producto
UNION ALL SELECT 'ventas',       COUNT(*) FROM venta
UNION ALL SELECT 'resenas',      COUNT(*) FROM resena
UNION ALL SELECT 'devoluciones', COUNT(*) FROM devolucion;
