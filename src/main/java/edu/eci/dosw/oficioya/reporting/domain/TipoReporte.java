package edu.eci.dosw.oficioya.reporting.domain;

import java.util.List;

/**
 * Reportes disponibles (RF-01). Cada uno agrupa los indicadores y estadísticas de un tema.
 */
public enum TipoReporte {
    SERVICIOS("Reporte de solicitudes y servicios",
            "Servicios cumplidos, cancelaciones, flujo de trabajo, demanda por oficio y zona, y tarifas (RF-05).",
            List.of(TipoIndicador.SERVICIOS_CUMPLIDOS, TipoIndicador.TASA_CANCELACION,
                    TipoIndicador.CATEGORIA_MAS_APETECIDA, TipoIndicador.TARIFA_PROMEDIO),
            List.of(TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_DIA, TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_OFICIO,
                    TipoEstadistica.SOLICITUDES_POR_ESTADO, TipoEstadistica.SOLICITUDES_POR_OFICIO,
                    TipoEstadistica.SOLICITUDES_POR_ZONA, TipoEstadistica.SOLICITUDES_POR_FRANJA,
                    TipoEstadistica.TARIFA_PROMEDIO_POR_OFICIO)),
    RESENAS("Reporte de calificaciones y reputación",
            "Calificación promedio y reseñas reportadas (RF-06).",
            List.of(TipoIndicador.CALIFICACION_PROMEDIO, TipoIndicador.RESENAS_REPORTADAS),
            List.of(TipoEstadistica.REPORTES_DE_RESENAS_POR_ESTADO)),
    TRABAJADORES("Reporte de oficios y trabajadores",
            "Trabajadores activos y pausados, y oferta por oficio (RF-07).",
            List.of(TipoIndicador.TRABAJADORES_ACTIVOS, TipoIndicador.TRABAJADORES_PAUSADOS),
            List.of(TipoEstadistica.TRABAJADORES_POR_OFICIO)),
    USUARIOS("Reporte de cuentas y usuarios",
            "Cuentas activas e inactivas y usuarios activos por día.",
            List.of(TipoIndicador.CUENTAS_ACTIVAS, TipoIndicador.CUENTAS_INACTIVAS,
                    TipoIndicador.PROMEDIO_USUARIOS_ACTIVOS),
            List.of(TipoEstadistica.CUENTAS_POR_ESTADO, TipoEstadistica.USUARIOS_ACTIVOS_POR_DIA)),
    GENERAL("Resumen de la plataforma",
            "Todos los indicadores y estadísticas (vista de resumen del administrador).",
            List.of(TipoIndicador.values()),
            List.of(TipoEstadistica.values()));

    private final String titulo;
    private final String descripcion;
    private final List<TipoIndicador> indicadores;
    private final List<TipoEstadistica> estadisticas;

    TipoReporte(String titulo, String descripcion, List<TipoIndicador> indicadores,
                List<TipoEstadistica> estadisticas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.indicadores = indicadores;
        this.estadisticas = estadisticas;
    }

    public String titulo() {
        return titulo;
    }

    public String descripcion() {
        return descripcion;
    }

    public List<TipoIndicador> indicadores() {
        return indicadores;
    }

    public List<TipoEstadistica> estadisticas() {
        return estadisticas;
    }
}
