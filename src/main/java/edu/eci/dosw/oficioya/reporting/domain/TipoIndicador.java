package edu.eci.dosw.oficioya.reporting.domain;

/**
 * Indicadores de desempeño disponibles (los tres primeros son las tarjetas del mockup).
 */
public enum TipoIndicador {
    SERVICIOS_CUMPLIDOS("Total de servicios cumplidos"),
    TRABAJADORES_PAUSADOS("Trabajadores pausados"),
    RESENAS_REPORTADAS("Reseñas reportadas"),
    TASA_CANCELACION("Tasa de cancelación de solicitudes"),
    TRABAJADORES_ACTIVOS("Trabajadores activos"),
    CALIFICACION_PROMEDIO("Calificación promedio");

    private final String nombre;

    TipoIndicador(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }
}
