package ar.edu.uade.repuestos.negocio;

import ar.edu.uade.repuestos.datos.DevolucionRepository;
import ar.edu.uade.repuestos.datos.ResenaRepository;
import ar.edu.uade.repuestos.modelo.Devolucion;
import ar.edu.uade.repuestos.modelo.EstadoDevolucion;
import ar.edu.uade.repuestos.modelo.EstadoResena;
import ar.edu.uade.repuestos.modelo.Resena;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

// Fachada del panel de moderacion. La autorizacion la aplica el contenedor, no un if.
@Stateless
public class ServicioDeModeracion {
    private static final Logger LOG = Logger.getLogger(ServicioDeModeracion.class.getName());

    @Inject
    private ResenaRepository resenaRepository;

    @Inject
    private DevolucionRepository devolucionRepository;

    @Inject
    private ServicioDeReembolsos servicioDeReembolsos;

    @Inject
    private ServicioDeNotificaciones servicioDeNotificaciones;

    @Inject
    private ServicioDeVentas servicioDeVentas;

    @PostConstruct
    void alCrear() {
        LOG.info(() -> "[CICLO DE VIDA] @PostConstruct ServicioDeModeracion (stateless) instancia #"
                + Integer.toHexString(System.identityHashCode(this)) + " entra al pool.");
    }

    @PreDestroy
    void alDestruir() {
        LOG.info(() -> "[CICLO DE VIDA] @PreDestroy ServicioDeModeracion (stateless) instancia #"
                + Integer.toHexString(System.identityHashCode(this)) + " sale del pool.");
    }

    @RolesAllowed("MODERADOR")
    public List<Resena> resenasPendientes() {
        return resenaRepository.porEstado(EstadoResena.PENDIENTE);
    }

    @RolesAllowed("MODERADOR")
    public List<Devolucion> devolucionesPendientes() {
        return devolucionRepository.porEstado(EstadoDevolucion.PENDIENTE);
    }

    @RolesAllowed("MODERADOR")
    public void aprobarResena(Long resenaId) {
        Resena resena = buscarResena(resenaId);
        exigirPendiente(resena);
        resena.aprobar();
        resenaRepository.guardar(resena);
        servicioDeNotificaciones.resenaResuelta(resena);
    }

    @RolesAllowed("MODERADOR")
    public void rechazarResena(Long resenaId, String motivo) {
        Resena resena = buscarResena(resenaId);
        exigirPendiente(resena);
        resena.rechazar(motivo);
        resenaRepository.guardar(resena);
        servicioDeNotificaciones.resenaResuelta(resena);
    }

    // Calcular, aprobar, guardar, reponer stock, reembolsar y notificar: una sola transaccion.
    @RolesAllowed("MODERADOR")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void aprobarDevolucion(Long devolucionId) {
        Devolucion devolucion = buscarDevolucion(devolucionId);
        exigirPendiente(devolucion);

        BigDecimal monto = servicioDeReembolsos.calcularMonto(devolucion);
        devolucion.aprobar(monto);
        devolucionRepository.guardar(devolucion);

        servicioDeVentas.reponerStockDe(devolucion.getVenta());
        servicioDeReembolsos.emitir(devolucion);
        servicioDeNotificaciones.devolucionResuelta(devolucion);
    }

    @RolesAllowed("MODERADOR")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void rechazarDevolucion(Long devolucionId) {
        Devolucion devolucion = buscarDevolucion(devolucionId);
        exigirPendiente(devolucion);

        devolucion.rechazar();
        devolucionRepository.guardar(devolucion);
        servicioDeNotificaciones.devolucionResuelta(devolucion);
    }

    private Resena buscarResena(Long id) {
        return resenaRepository.porId(id)
                .orElseThrow(() -> new ReglaNegocioException("La resena no existe."));
    }

    private Devolucion buscarDevolucion(Long id) {
        return devolucionRepository.porId(id)
                .orElseThrow(() -> new ReglaNegocioException("La solicitud no existe."));
    }

    private void exigirPendiente(Resena resena) {
        if (resena.getEstado() != EstadoResena.PENDIENTE) {
            throw new ReglaNegocioException("Esa resena ya fue resuelta.");
        }
    }

    private void exigirPendiente(Devolucion devolucion) {
        if (devolucion.getEstado() != EstadoDevolucion.PENDIENTE) {
            throw new ReglaNegocioException("Esa solicitud ya fue resuelta.");
        }
    }
}
