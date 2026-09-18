package ar.edu.uade.repuestos.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "producto")
@NamedQueries({
    @NamedQuery(name = "Producto.todos",
                query = "SELECT p FROM Producto p ORDER BY p.nombre"),
    @NamedQuery(name = "Producto.buscar",
                query = "SELECT p FROM Producto p "
                      + "WHERE LOWER(p.nombre) LIKE :texto "
                      + "   OR LOWER(p.categoria) LIKE :texto "
                      + "ORDER BY p.nombre")
})
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(length = 60)
    private String categoria;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    public Producto() {
    }

    public boolean hayStock(int cantidad) {
        return stock >= cantidad;
    }

    public void descontarStock(int cantidad) {
        this.stock -= cantidad;
    }

    public void reponerStock(int cantidad) {
        this.stock += cantidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        Producto otro = (Producto) o;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
