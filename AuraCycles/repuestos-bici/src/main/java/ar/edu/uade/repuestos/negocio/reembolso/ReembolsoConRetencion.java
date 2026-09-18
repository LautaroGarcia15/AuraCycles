package ar.edu.uade.repuestos.negocio.reembolso;

import ar.edu.uade.repuestos.modelo.MotivoDevolucion;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class ReembolsoConRetencion implements PoliticaDeReembolso {
    private static final BigDecimal RETENCION = new BigDecimal("0.10");

    @Override
    public boolean aplicaA(MotivoDevolucion motivo) {
        return motivo == MotivoDevolucion.ARREPENTIMIENTO;
    }

    @Override
    public BigDecimal calcular(BigDecimal importePagado) {
        BigDecimal retenido = importePagado.multiply(RETENCION);
        return importePagado.subtract(retenido).setScale(2, RoundingMode.HALF_UP);
    }
}
