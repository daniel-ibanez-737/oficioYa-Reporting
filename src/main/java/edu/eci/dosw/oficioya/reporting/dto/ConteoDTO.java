package edu.eci.dosw.oficioya.reporting.dto;

/**
 * Conteo agregado que envían los otros dominios: la etiqueta puede ser una fecha (yyyy-MM-dd), un oficio, etc.
 */
public record ConteoDTO(String etiqueta, long cantidad) {
}
