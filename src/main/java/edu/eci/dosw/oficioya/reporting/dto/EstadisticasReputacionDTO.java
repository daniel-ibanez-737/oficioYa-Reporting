package edu.eci.dosw.oficioya.reporting.dto;

/**
 * Lo que Reputation (Squad 5) entrega vía Orchestrator para un periodo
 * ({@code GET /api/v1/reputation/estadisticas}).
 */
public record EstadisticasReputacionDTO(
        long totalResenas,
        double calificacionPromedio,
        long resenasReportadas,
        long reportesPendientes,
        long reportesResueltos) {
}
