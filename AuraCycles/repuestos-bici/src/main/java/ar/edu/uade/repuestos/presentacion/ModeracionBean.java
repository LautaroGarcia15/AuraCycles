package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Devolucion;
import ar.edu.uade.repuestos.modelo.Resena;
import ar.edu.uade.repuestos.negocio.ServicioDeModeracion;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBAccessException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("moderacionBean")
@ViewScoped
public class ModeracionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioDeModeracion servicioDeModeracion;

    private List<Resena> resenasPendientes;
    private List<Devolucion> devolucionesPendientes;

    private String solapa = "resenas";
    private String motivoRechazo;

    @PostConstruct
    public void cargar() {
        try {
            this.resenasPendientes = servicioDeModeracion.resenasPendientes();
            this.devolucionesPendientes = servicioDeModeracion.devolucionesPendientes();
        } catch (EJBAccessException e) {
            Mensajes.error("No tenes permiso para ver el panel de moderacion.");
        }
    }

    public void aprobarResena(Long id) {
        ejecutar(() -> servicioDeModeracion.aprobarResena(id), "Rese\u00f1a aprobada.");
    }

    public void rechazarResena(Long id) {
        ejecutar(() -> servicioDeModeracion.rechazarResena(id, motivoRechazo), "Rese\u00f1a rechazada.");
    }

    public void aprobarDevolucion(Long id) {
        ejecutar(() -> servicioDeModeracion.aprobarDevolucion(id),
                 "Devoluci\u00f3n aprobada. Se emiti\u00f3 el reembolso y se repuso el stock.");
    }

    public void rechazarDevolucion(Long id) {
        ejecutar(() -> servicioDeModeracion.rechazarDevolucion(id), "Devoluci\u00f3n rechazada.");
    }

    private void ejecutar(Runnable accion, String mensajeOk) {
        try {
            accion.run();
            Mensajes.ok(mensajeOk);
            cargar();
        } catch (EJBAccessException e) {
            Mensajes.error("No tenes permiso para hacer esa operacion.");
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        }
    }

    public String estrellas(int puntaje) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= puntaje ? '\u2605' : '\u2606');
        }
        return sb.toString();
    }

    public int getCantidadResenas() {
        return resenasPendientes == null ? 0 : resenasPendientes.size();
    }

    public int getCantidadDevoluciones() {
        return devolucionesPendientes == null ? 0 : devolucionesPendientes.size();
    }

    public List<Resena> getResenasPendientes() { return resenasPendientes; }

    public List<Devolucion> getDevolucionesPendientes() { return devolucionesPendientes; }

    public String getSolapa() { return solapa; }
    public void setSolapa(String solapa) { this.solapa = solapa; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
