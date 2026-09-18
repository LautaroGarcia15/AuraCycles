package ar.edu.uade.repuestos.datos;

import ar.edu.uade.repuestos.modelo.Producto;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class ProductoRepository {
    @PersistenceContext(unitName = "repuestosPU")
    private EntityManager em;

    public Producto guardar(Producto producto) {
        if (producto.getId() == null) {
            em.persist(producto);
            return producto;
        }
        return em.merge(producto);
    }

    public Optional<Producto> porId(Long id) {
        return Optional.ofNullable(em.find(Producto.class, id));
    }

    public List<Producto> todos() {
        return em.createNamedQuery("Producto.todos", Producto.class).getResultList();
    }

    public List<Producto> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return todos();
        }
        return em.createNamedQuery("Producto.buscar", Producto.class)
                 .setParameter("texto", "%" + texto.toLowerCase() + "%")
                 .getResultList();
    }

    // Bloqueo pesimista: dos compras del ultimo repuesto no pueden dejar stock negativo.
    public Optional<Producto> porIdBloqueado(Long id) {
        return Optional.ofNullable(
            em.find(Producto.class, id, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE));
    }
}
