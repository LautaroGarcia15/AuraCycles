package ar.edu.uade.repuestos.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "resena")
@NamedQueries({
    @NamedQuery(name = "Resena.aprobadasPorProducto",
                query = "SELECT r FROM Resena r "
                      + "WHERE r.producto.id = :productoId AND r.estado = "
                      + "ar.edu.uade.repuestos.modelo.EstadoResena.APROBADA "
                      + "ORDER BY r.fecha DESC"),
    @NamedQuery(name = "Resena.porEstado",
                query = "SELECT r FROM Resena r WHERE r.estado = :estado ORDER BY r.fecha ASC"),
    @NamedQuery(name = "Resena.promedioProducto",
                query = "SELECT AVG(r.puntaje) FROM Resena r "
                      + "WHERE r.producto.id = :productoId AND r.estado = "
                      + "ar.edu.uade.repuestos.modelo.EstadoResena.APROBADA")
})
public class Resena implements Serializable {
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
    private int puntaje;

    @Column(length = 160)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoResena estado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "motivo_rechazo", length = 200)
    private String motivoRechazo;

    public Resena() {
    }

    public Resena(Usuario usuario, Producto producto, int puntaje, String titulo, String texto) {
        this.usuario = usuario;
        this.producto = producto;
        this.puntaje = puntaje;
        this.titulo = titulo;
        this.texto = texto;
        this.estado = EstadoResena.PENDIENTE;
        this.fecha = LocalDateTime.now();
    }

    public void aprobar() {
        this.estado = EstadoResena.APROBADA;
        this.motivoRechazo = null;
    }

    public void rechazar(String motivo) {
        this.estado = EstadoResena.RECHAZADA;
        this.motivoRechazo = motivo;
    }

    @Transient
    public String getCodigo() {
        return id == null ? "-" : String.format("RES-%06d", id);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public EstadoResena getEstado() { return estado; }
    public void setEstado(EstadoResena estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resena)) return false;
        Resena otra = (Resena) o;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
