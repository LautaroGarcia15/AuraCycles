package ar.edu.uade.repuestos.util;

import jakarta.ejb.ApplicationException;

// rollback = true: si una regla falla a mitad del flujo, se deshace toda la transaccion.
@ApplicationException(rollback = true)
public class ReglaNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
