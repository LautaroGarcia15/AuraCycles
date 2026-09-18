package ar.edu.uade.repuestos.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "venta")
@NamedQueries({
    @NamedQuery(name = "Venta.porUsuario",
                query = "SELECT v FROM Venta v WHERE v.usuario.id = :usuarioId "
                      + "ORDER BY v.fecha DESC"),
    @NamedQuery(name = "Venta.comproProducto",
                query = "SELECT COUNT(v) FROM Venta v "
                      + "WHERE v.usuario.id = :usuarioId AND v.producto.id = :productoId")
})
public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Venta() {
    }

    public Venta(Usuario usuario, Producto producto, int cantidad, BigDecimal importe) {
        this.usuario = usuario;
        this.producto = producto;
        this.cantidad = cantidad;
        this.importe = importe;
        this.fecha = LocalDateTime.now();
    }

    @Transient
    public String getNumeroOrden() {
        return id == null ? "-" : String.format("ORD-%06d", id);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta otra = (Venta) o;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
