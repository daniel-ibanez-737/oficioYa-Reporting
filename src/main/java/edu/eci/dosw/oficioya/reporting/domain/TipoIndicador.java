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
    CALIFICACION_PROMEDIO("Calificación promedio"),
    CUENTAS_ACTIVAS("Cuentas activas"),
    CUENTAS_INACTIVAS("Cuentas inactivas"),
    PROMEDIO_USUARIOS_ACTIVOS("Promedio de usuarios activos"),
    CATEGORIA_MAS_APETECIDA("Categoría más apetecida"),
    TARIFA_PROMEDIO("Tarifa promedio");

    private final String nombre;

    TipoIndicador(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }
}
