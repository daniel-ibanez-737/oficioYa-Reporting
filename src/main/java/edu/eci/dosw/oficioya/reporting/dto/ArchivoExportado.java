package edu.eci.dosw.oficioya.reporting.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * Archivo listo para descargar.
 * Como el contenido es un arreglo, equals, hashCode y toString se escriben a mano para comparar sus bytes.
 */
public record ArchivoExportado(String nombre, String tipoContenido, byte[] contenido) {

    @Override
    public boolean equals(Object otro) {
        return otro instanceof ArchivoExportado(String otroNombre, String otroTipo, byte[] otroContenido)
                && Objects.equals(nombre, otroNombre)
                && Objects.equals(tipoContenido, otroTipo)
                && Arrays.equals(contenido, otroContenido);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(nombre, tipoContenido) + Arrays.hashCode(contenido);
    }

    @Override
    public String toString() {
        return "ArchivoExportado[nombre=" + nombre + ", tipoContenido=" + tipoContenido
                + ", bytes=" + (contenido == null ? 0 : contenido.length) + "]";
    }
}
