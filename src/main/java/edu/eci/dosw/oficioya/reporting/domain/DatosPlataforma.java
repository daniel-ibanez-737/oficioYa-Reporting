package edu.eci.dosw.oficioya.reporting.domain;

import java.util.List;
import java.util.Map;

/**
 * Datos agregados de la plataforma para un periodo, ya traídos de Service, Worker y Reputation.
 * Solo contiene conteos y promedios: ningún dato personal (RNF-05).
 */
@SuppressWarnings("java:S107")
public record DatosPlataforma(
        PeriodoConsulta periodo,
        Map<String, Long> solicitudesPorEstado,
        List<PuntoEstadistico> cumplidosPorDia,
        List<PuntoEstadistico> cumplidosPorOficio,
        long trabajadoresActivos,
        long pausadosVoluntarios,
        long pausadosPorReputacion,
        List<PuntoEstadistico> trabajadoresPorOficio,
        double calificacionPromedio,
        long resenasReportadas,
        long reportesPendientes,
        long reportesResueltos) {

    /** SUPUESTO: nombres de los estados de solicitud en Service (pendiente de confirmar con el Squad 3). */
    public static final String ESTADO_CUMPLIDA = "CUMPLIDA";
    public static final String ESTADO_CANCELADA = "CANCELADA";

    public DatosPlataforma {
        solicitudesPorEstado = Map.copyOf(solicitudesPorEstado);
        cumplidosPorDia = List.copyOf(cumplidosPorDia);
        cumplidosPorOficio = List.copyOf(cumplidosPorOficio);
        trabajadoresPorOficio = List.copyOf(trabajadoresPorOficio);
    }

    public long serviciosCumplidos() {
        return solicitudesPorEstado.getOrDefault(ESTADO_CUMPLIDA, 0L);
    }

    public long solicitudesCanceladas() {
        return solicitudesPorEstado.getOrDefault(ESTADO_CANCELADA, 0L);
    }

    public long totalSolicitudes() {
        return solicitudesPorEstado.values().stream().mapToLong(Long::longValue).sum();
    }

    public long trabajadoresPausados() {
        return pausadosVoluntarios + pausadosPorReputacion;
    }

    /** Arma la estadística pedida con los datos de este periodo (RF-03). */
    public Estadistica estadistica(TipoEstadistica tipo) {
        List<PuntoEstadistico> puntos = switch (tipo) {
            case SERVICIOS_CUMPLIDOS_POR_DIA -> cumplidosPorDia;
            case SERVICIOS_CUMPLIDOS_POR_OFICIO -> cumplidosPorOficio;
            case SOLICITUDES_POR_ESTADO -> solicitudesPorEstado.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> new PuntoEstadistico(e.getKey(), e.getValue()))
                    .toList();
            case TRABAJADORES_POR_OFICIO -> trabajadoresPorOficio;
            case REPORTES_DE_RESENAS_POR_ESTADO -> List.of(
                    new PuntoEstadistico("Pendientes", reportesPendientes),
                    new PuntoEstadistico("Resueltos", reportesResueltos));
        };
        return new Estadistica(tipo, puntos);
    }
}
