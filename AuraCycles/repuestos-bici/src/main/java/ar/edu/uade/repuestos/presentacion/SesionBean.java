package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

// Estado de presentacion: quien mira la pantalla. El estado de negocio vive en ServicioDeDevoluciones.
@Named("sesion")
@SessionScoped
public class SesionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Usuario usuario;
    private boolean temaOscuro;

    public boolean isLogueado() {
        return usuario != null;
    }

    public boolean isModerador() {
        return usuario != null && usuario.esModerador();
    }

    public void alternarTema() {
        this.temaOscuro = !this.temaOscuro;
    }

    public String getClaseTema() {
        return temaOscuro ? "tema-oscuro" : "tema-claro";
    }

    public String getNombre() {
        return usuario == null ? "" : usuario.getNombre();
    }

    public Long getUsuarioId() {
        return usuario == null ? null : usuario.getId();
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public boolean isTemaOscuro() { return temaOscuro; }
    public void setTemaOscuro(boolean temaOscuro) { this.temaOscuro = temaOscuro; }
}
