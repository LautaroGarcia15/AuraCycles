package ar.edu.uade.repuestos.datos;

import ar.edu.uade.repuestos.modelo.Venta;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class VentaRepository {
    @PersistenceContext(unitName = "repuestosPU")
    private EntityManager em;

    public Venta guardar(Venta venta) {
        if (venta.getId() == null) {
            em.persist(venta);
            return venta;
        }
        return em.merge(venta);
    }

    public Optional<Venta> porId(Long id) {
        return Optional.ofNullable(em.find(Venta.class, id));
    }

    public List<Venta> porUsuario(Long usuarioId) {
        return em.createNamedQuery("Venta.porUsuario", Venta.class)
                 .setParameter("usuarioId", usuarioId)
                 .getResultList();
    }

    public boolean usuarioComproProducto(Long usuarioId, Long productoId) {
        Long cantidad = em.createNamedQuery("Venta.comproProducto", Long.class)
                          .setParameter("usuarioId", usuarioId)
                          .setParameter("productoId", productoId)
                          .getSingleResult();
        return cantidad != null && cantidad > 0;
    }
}
