package ar.edu.uade.repuestos.datos;

import ar.edu.uade.repuestos.modelo.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class UsuarioRepository {
    @PersistenceContext(unitName = "repuestosPU")
    private EntityManager em;

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
            return usuario;
        }
        return em.merge(usuario);
    }

    public Optional<Usuario> porId(Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public Optional<Usuario> porEmail(String email) {
        try {
            return Optional.of(em.createNamedQuery("Usuario.porEmail", Usuario.class)
                                 .setParameter("email", email)
                                 .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existeEmail(String email) {
        return porEmail(email).isPresent();
    }
}
