package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Devolucion;
import ar.edu.uade.repuestos.modelo.MotivoDevolucion;
import ar.edu.uade.repuestos.negocio.ServicioDeDevoluciones;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("devolucionBean")
@ViewScoped
public class DevolucionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioDeDevoluciones servicioDeDevoluciones;

    private Long ventaId;
    private MotivoDevolucion motivo;
    private String comentario;

    private Devolucion confirmada;

    public void iniciarFlujo() {
        if (ventaId == null || servicioDeDevoluciones.hayBorrador()) {
            return;
        }
        try {
            servicioDeDevoluciones.seleccionarCompra(ventaId);
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        }
    }

    public String enviar() {
        try {
            servicioDeDevoluciones.cargarMotivo(motivo, comentario);
            this.confirmada = servicioDeDevoluciones.confirmar();
            servicioDeDevoluciones.finalizar();
            return "devolucion-ok?faces-redirect=true&codigo=" + confirmada.getCodigo();
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
            return null;
        }
    }

    public String cancelar() {
        servicioDeDevoluciones.finalizar();
        return "compras?faces-redirect=true";
    }

    public MotivoDevolucion[] getMotivos() {
        return MotivoDevolucion.values();
    }

    public boolean isHayBorrador() {
        return servicioDeDevoluciones.hayBorrador();
    }

    public ar.edu.uade.repuestos.modelo.Venta getVenta() {
        return servicioDeDevoluciones.getVentaSeleccionada();
    }

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public MotivoDevolucion getMotivo() { return motivo; }
    public void setMotivo(MotivoDevolucion motivo) { this.motivo = motivo; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Devolucion getConfirmada() { return confirmada; }
}
