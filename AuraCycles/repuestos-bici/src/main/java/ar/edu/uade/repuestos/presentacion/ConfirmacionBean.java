package ar.edu.uade.repuestos.presentacion;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("confirmacionBean")
@ViewScoped
public class ConfirmacionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
