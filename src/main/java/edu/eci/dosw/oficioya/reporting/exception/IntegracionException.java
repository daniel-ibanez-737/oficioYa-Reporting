package edu.eci.dosw.oficioya.reporting.exception;

/**
 * Falla al consultar otro dominio a través del Orchestrator. Se traduce a HTTP 502.
 */
public class IntegracionException extends RuntimeException {

    public IntegracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
