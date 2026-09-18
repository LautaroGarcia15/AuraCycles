package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Usuario;
import ar.edu.uade.repuestos.negocio.ServicioDeUsuarios;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("perfilBean")
@ViewScoped
public class PerfilBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioDeUsuarios servicioDeUsuarios;

    @Inject
    private SesionBean sesion;

    private String nombre;
    private String email;

    private String passwordActual;
    private String passwordNueva;
    private String passwordRepetida;

    @PostConstruct
    public void cargar() {
        Usuario usuario = sesion.getUsuario();
        if (usuario != null) {
            this.nombre = usuario.getNombre();
            this.email = usuario.getEmail();
        }
    }

    public void guardarDatos() {
        try {
            Usuario actualizado = servicioDeUsuarios.actualizarPerfil(
                sesion.getUsuarioId(), nombre, email);
            sesion.setUsuario(actualizado);
            Mensajes.ok("Guardamos tus datos.");
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        }
    }

    public void guardarPassword() {
        if (passwordNueva == null || !passwordNueva.equals(passwordRepetida)) {
            Mensajes.error("Las contrase\u00f1as nuevas no coinciden.");
            return;
        }
        try {
            servicioDeUsuarios.cambiarPassword(sesion.getUsuarioId(), passwordActual, passwordNueva);
            this.passwordActual = null;
            this.passwordNueva = null;
            this.passwordRepetida = null;
            Mensajes.ok("Cambiamos tu contrase\u00f1a.");
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordActual() { return passwordActual; }
    public void setPasswordActual(String v) { this.passwordActual = v; }

    public String getPasswordNueva() { return passwordNueva; }
    public void setPasswordNueva(String v) { this.passwordNueva = v; }

    public String getPasswordRepetida() { return passwordRepetida; }
    public void setPasswordRepetida(String v) { this.passwordRepetida = v; }
}
