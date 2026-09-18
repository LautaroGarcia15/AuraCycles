package ar.edu.uade.repuestos.negocio.reembolso;

import ar.edu.uade.repuestos.modelo.MotivoDevolucion;
import java.math.BigDecimal;

// Una implementacion por tipo de motivo. Sumar una regla es agregar una clase.
public interface PoliticaDeReembolso {
    boolean aplicaA(MotivoDevolucion motivo);

    BigDecimal calcular(BigDecimal importePagado);
}
