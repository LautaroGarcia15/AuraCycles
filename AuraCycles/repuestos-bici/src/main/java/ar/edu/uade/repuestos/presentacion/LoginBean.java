package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Usuario;
import ar.edu.uade.repuestos.negocio.ServicioDeUsuarios;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Optional;

@Named("loginBean")
@ViewScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioDeUsuarios servicioDeUsuarios;

    @Inject
    private SesionBean sesion;

    private String email;
    private String password;

    private String nombreNuevo;
    private String emailNuevo;
    private String passwordNueva;
    private boolean mostrandoRegistro;

    public String ingresar() {
        Optional<Usuario> usuario = servicioDeUsuarios.autenticar(email, password);

        if (usuario.isEmpty()) {
            Mensajes.error("Email o contrase\u00f1a incorrectos.");
            return null;
        }
        if (!ingresarEnContenedor(usuario.get().getEmail(), password)) {
            return null;
        }

        sesion.setUsuario(usuario.get());
        return "productos?faces-redirect=true";
    }

    public String registrarse() {
        try {
            Usuario nuevo = servicioDeUsuarios.registrar(emailNuevo, passwordNueva, nombreNuevo);
            if (!ingresarEnContenedor(nuevo.getEmail(), passwordNueva)) {
                return null;
            }
            sesion.setUsuario(nuevo);
            return "productos?faces-redirect=true";
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
            return null;
        }
    }

    public String salir() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ((HttpServletRequest) ec.getRequest()).logout();
        } catch (ServletException e) {
        }
        ec.invalidateSession();
        return "login?faces-redirect=true";
    }

    // Sin request.login() el contenedor no sabe quien entro y @RolesAllowed rechaza al moderador.
    private boolean ingresarEnContenedor(String usuario, String clave) {
        HttpServletRequest request = (HttpServletRequest)
                FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try {
            request.getSession(true);
            if (request.getUserPrincipal() != null) {
                request.logout();
            }
            request.login(usuario, clave);
            return true;
        } catch (ServletException e) {
            Mensajes.error("El servidor rechaz\u00f3 el ingreso: revisar repuestos-realm en WildFly (README, paso 4).");
            return false;
        }
    }

    public void alternarRegistro() {
        this.mostrandoRegistro = !this.mostrandoRegistro;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreNuevo() { return nombreNuevo; }
    public void setNombreNuevo(String nombreNuevo) { this.nombreNuevo = nombreNuevo; }

    public String getEmailNuevo() { return emailNuevo; }
    public void setEmailNuevo(String emailNuevo) { this.emailNuevo = emailNuevo; }

    public String getPasswordNueva() { return passwordNueva; }
    public void setPasswordNueva(String passwordNueva) { this.passwordNueva = passwordNueva; }

    public boolean isMostrandoRegistro() { return mostrandoRegistro; }
    public void setMostrandoRegistro(boolean v) { this.mostrandoRegistro = v; }
}
