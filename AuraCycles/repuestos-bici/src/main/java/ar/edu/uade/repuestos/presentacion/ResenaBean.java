package ar.edu.uade.repuestos.presentacion;

import ar.edu.uade.repuestos.modelo.Producto;
import ar.edu.uade.repuestos.negocio.ServicioDeProductos;
import ar.edu.uade.repuestos.negocio.ServicioDeResenas;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("resenaBean")
@ViewScoped
public class ResenaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ServicioDeResenas servicioDeResenas;

    @Inject
    private ServicioDeProductos servicioDeProductos;

    @Inject
    private SesionBean sesion;

    private Long productoId;
    private Producto producto;
    private int puntaje = 5;
    private String titulo;
    private String texto;

    public void cargarProducto() {
        if (productoId == null) {
            return;
        }
        try {
            this.producto = servicioDeProductos.porId(productoId);
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        }
    }

    public String publicar() {
        try {
            servicioDeResenas.escribir(sesion.getUsuarioId(), productoId, puntaje, titulo, texto);
            Mensajes.ok("Enviamos tu rese\u00f1a. Se publica cuando la apruebe un moderador.");
            return "producto?faces-redirect=true&productoId=" + productoId;
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
            return null;
        }
    }

    public int[] getPuntajes() {
        return new int[]{1, 2, 3, 4, 5};
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Producto getProducto() { return producto; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
