package edu.eci.dosw.oficioya.reporting.domain;

/**
 * Estadísticas agregadas disponibles.
 */
public enum TipoEstadistica {
    SERVICIOS_CUMPLIDOS_POR_DIA("Servicios cumplidos por día", "Desempeño de la plataforma en el periodo"),
    SERVICIOS_CUMPLIDOS_POR_OFICIO("Servicios cumplidos por oficio", "Oficios con más actividad en el periodo"),
    SOLICITUDES_POR_ESTADO("Solicitudes por estado", "Solicitudes creadas en el periodo según su estado actual"),
    TRABAJADORES_POR_OFICIO("Trabajadores activos por oficio", "Oferta actual de trabajadores por oficio"),
    REPORTES_DE_RESENAS_POR_ESTADO("Reportes de reseñas por estado", "Reportes recibidos en el periodo");

    private final String nombre;
    private final String descripcion;

    TipoEstadistica(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String nombre() {
        return nombre;
    }

    public String descripcion() {
        return descripcion;
    }
}
