package edu.eci.dosw.oficioya.reporting.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Datos agregados de la plataforma para un periodo, ya traídos de Service, Worker, Reputation e Identity.
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
        long reportesResueltos,
        DatosDemanda demanda) {

    /** SUPUESTO: nombres de los estados de solicitud en Service (pendiente de confirmar con el Squad 3). */
    public static final String ESTADO_CUMPLIDA = "CUMPLIDA";
    public static final String ESTADO_CANCELADA = "CANCELADA";

    /** Orden del flujo de trabajo de una solicitud; los estados que no estén aquí van al final. */
    // Estados de Service (Squad 4), ya en español: Pendiente, Aceptada, Conflicto, Cumplida y los finales.
    static final List<String> FLUJO_DE_TRABAJO = List.of("PENDIENTE", "ACEPTADA", "CONFLICTO", ESTADO_CUMPLIDA,
            "RECHAZADA", ESTADO_CANCELADA, "EXPIRADA", "ELIMINADA");

    private static final Comparator<Map.Entry<String, Long>> POR_FLUJO = Comparator
            .<Map.Entry<String, Long>>comparingInt(e -> FLUJO_DE_TRABAJO.contains(e.getKey())
                    ? FLUJO_DE_TRABAJO.indexOf(e.getKey()) : FLUJO_DE_TRABAJO.size())
            .thenComparing(Map.Entry.comparingByKey());

    public DatosPlataforma {
        solicitudesPorEstado = Map.copyOf(solicitudesPorEstado);
        cumplidosPorDia = List.copyOf(cumplidosPorDia);
        cumplidosPorOficio = List.copyOf(cumplidosPorOficio);
        trabajadoresPorOficio = List.copyOf(trabajadoresPorOficio);
        demanda = demanda == null ? DatosDemanda.VACIOS : demanda;
    }

    /** Datos sin cuentas ni demanda (los que existían antes de la review del Sprint 4). */
    public DatosPlataforma(PeriodoConsulta periodo, Map<String, Long> solicitudesPorEstado,
                           List<PuntoEstadistico> cumplidosPorDia, List<PuntoEstadistico> cumplidosPorOficio,
                           long trabajadoresActivos, long pausadosVoluntarios, long pausadosPorReputacion,
                           List<PuntoEstadistico> trabajadoresPorOficio, double calificacionPromedio,
                           long resenasReportadas, long reportesPendientes, long reportesResueltos) {
        this(periodo, solicitudesPorEstado, cumplidosPorDia, cumplidosPorOficio, trabajadoresActivos,
                pausadosVoluntarios, pausadosPorReputacion, trabajadoresPorOficio, calificacionPromedio,
                resenasReportadas, reportesPendientes, reportesResueltos, DatosDemanda.VACIOS);
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
                    .sorted(POR_FLUJO)
                    .map(e -> new PuntoEstadistico(e.getKey(), e.getValue()))
                    .toList();
            case TRABAJADORES_POR_OFICIO -> trabajadoresPorOficio;
            case REPORTES_DE_RESENAS_POR_ESTADO -> List.of(
                    new PuntoEstadistico("Pendientes", reportesPendientes),
                    new PuntoEstadistico("Resueltos", reportesResueltos));
            case CUENTAS_POR_ESTADO -> List.of(
                    new PuntoEstadistico("Activas", demanda.cuentasActivas()),
                    new PuntoEstadistico("Inactivas", demanda.cuentasInactivas()));
            case USUARIOS_ACTIVOS_POR_DIA -> demanda.usuariosActivosPorDia();
            case SOLICITUDES_POR_OFICIO -> demanda.solicitudesPorOficio();
            case SOLICITUDES_POR_ZONA -> demanda.solicitudesPorZona();
            case SOLICITUDES_POR_FRANJA -> demanda.solicitudesPorFranja();
            case TARIFA_PROMEDIO_POR_OFICIO -> demanda.tarifaPromedioPorOficio();
        };
        return new Estadistica(tipo, puntos);
    }
}
