package ar.edu.uade.repuestos.presentacion;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

final class Mensajes {
    private Mensajes() {
    }

    static void error(String texto) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, texto, null));
    }

    static void ok(String texto) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, texto, null));
    }
}
