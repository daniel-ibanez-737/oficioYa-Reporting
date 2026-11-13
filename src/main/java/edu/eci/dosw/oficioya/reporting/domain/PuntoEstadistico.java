package edu.eci.dosw.oficioya.reporting.domain;

/**
 * Un valor de una estadística: por ejemplo, ("2026-10-04", 142) o ("Plomería", 35).
 */
public record PuntoEstadistico(String etiqueta, double valor) {
}
