package edu.eci.dosw.oficioya.reporting.dto;

/**
 * Archivo listo para descargar.
 */
public record ArchivoExportado(String nombre, String tipoContenido, byte[] contenido) {
}
