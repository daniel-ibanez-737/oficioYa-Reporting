package edu.eci.dosw.oficioya.reporting.exception;

/**
 * La consulta no es válida (por ejemplo, un periodo incoherente). Se traduce a HTTP 400.
 */
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
