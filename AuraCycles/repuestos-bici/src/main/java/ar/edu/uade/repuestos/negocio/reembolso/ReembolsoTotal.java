package ar.edu.uade.repuestos.negocio.reembolso;

import ar.edu.uade.repuestos.modelo.MotivoDevolucion;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class ReembolsoTotal implements PoliticaDeReembolso {
    @Override
    public boolean aplicaA(MotivoDevolucion motivo) {
        return motivo != MotivoDevolucion.ARREPENTIMIENTO;
    }

    @Override
    public BigDecimal calcular(BigDecimal importePagado) {
        return importePagado.setScale(2, RoundingMode.HALF_UP);
    }
}
