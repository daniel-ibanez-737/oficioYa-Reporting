package edu.eci.dosw.oficioya.reporting.domain;

/**
 * Estadísticas agregadas disponibles.
 */
public enum TipoEstadistica {
    SERVICIOS_CUMPLIDOS_POR_DIA("Servicios cumplidos por día", "Desempeño de la plataforma en el periodo"),
    SERVICIOS_CUMPLIDOS_POR_OFICIO("Servicios cumplidos por oficio", "Oficios con más actividad en el periodo"),
    SOLICITUDES_POR_ESTADO("Flujo de trabajo", "Solicitudes creadas en el periodo según la etapa en la que van"),
    TRABAJADORES_POR_OFICIO("Trabajadores activos por oficio", "Oferta actual de trabajadores por oficio"),
    REPORTES_DE_RESENAS_POR_ESTADO("Reportes de reseñas por estado", "Reportes recibidos en el periodo"),
    CUENTAS_POR_ESTADO("Cuentas activas e inactivas", "Estado actual de las cuentas de la plataforma"),
    USUARIOS_ACTIVOS_POR_DIA("Usuarios activos por día", "Usuarios que usaron la plataforma cada día del periodo"),
    SOLICITUDES_POR_OFICIO("Categorías más apetecidas", "Solicitudes creadas en el periodo por oficio"),
    SOLICITUDES_POR_ZONA("Zonas más concurridas", "Solicitudes creadas en el periodo por zona"),
    TARIFA_PROMEDIO_POR_OFICIO("Tarifa promedio por oficio", "Tarifa promedio (COP) de las solicitudes del periodo");

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
