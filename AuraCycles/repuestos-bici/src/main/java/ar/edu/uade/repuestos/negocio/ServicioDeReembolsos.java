package ar.edu.uade.repuestos.negocio;

import ar.edu.uade.repuestos.modelo.Devolucion;
import ar.edu.uade.repuestos.modelo.MotivoDevolucion;
import ar.edu.uade.repuestos.negocio.reembolso.PoliticaDeReembolso;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

// Strategy: la regla de cuanto se devuelve vive en las politicas, no aca.
@Stateless
public class ServicioDeReembolsos {
    @Inject
    private Instance<PoliticaDeReembolso> politicas;

    @Inject
    private ServicioDeNotificaciones servicioDeNotificaciones;

    public BigDecimal calcularMonto(Devolucion devolucion) {
        return politicaPara(devolucion.getMotivo())
                .calcular(devolucion.getVenta().getImporte());
    }

    public void emitir(Devolucion devolucion) {
        if (devolucion.getMontoReembolso() == null) {
            throw new ReglaNegocioException("La devolucion no tiene monto calculado.");
        }
        servicioDeNotificaciones.reembolsoEmitido(devolucion);
    }

    private PoliticaDeReembolso politicaPara(MotivoDevolucion motivo) {
        List<PoliticaDeReembolso> aplicables = politicas.stream()
                .filter(p -> p.aplicaA(motivo))
                .toList();
        if (aplicables.size() != 1) {
            throw new IllegalStateException(
                "Se esperaba una politica de reembolso para " + motivo + " y hay " + aplicables.size() + ".");
        }
        return aplicables.get(0);
    }
}
