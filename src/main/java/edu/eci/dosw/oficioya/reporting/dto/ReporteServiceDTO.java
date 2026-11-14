package edu.eci.dosw.oficioya.reporting.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reporte de solicitudes tal como lo entrega Service (Squad 4) en {@code GET /api/v1/services/reports?from=&to=}.
 * Se convierte al formato de Reporting con {@link #aEstadisticas()}: estados en español y horas agrupadas en las
 * franjas de 6 horas (las mismas del filtro de Search).
 *
 * @param total          solicitudes del periodo
 * @param byStatus       solicitudes por estado (PENDING, ACCEPTED, CONFLICT, CANCELED, REJECTED, COMPLETED, EXPIRED,
 *                       DELETED)
 * @param byNeighborhood solicitudes por barrio (nombre)
 * @param byHour         solicitudes por hora del día (0 a 23)
 */
public record ReporteServiceDTO(long total, Map<String, Long> byStatus, Map<String, Long> byNeighborhood,
                                Map<Integer, Long> byHour) {

    private static final Map<String, String> ESTADOS = Map.of(
            "PENDING", "PENDIENTE", "ACCEPTED", "ACEPTADA", "CONFLICT", "CONFLICTO", "CANCELED", "CANCELADA",
            "REJECTED", "RECHAZADA", "COMPLETED", "CUMPLIDA", "EXPIRED", "EXPIRADA", "DELETED", "ELIMINADA");

    /** Franjas de 6 horas en horario militar: la hora de inicio cuenta y la de fin no. */
    static final List<String> FRANJAS = List.of("Madrugada (00:00 – 06:00)", "Mañana (06:00 – 12:00)",
            "Tarde (12:00 – 18:00)", "Noche (18:00 – 24:00)");

    public EstadisticasServiciosDTO aEstadisticas() {
        Map<String, Long> porEstado = new LinkedHashMap<>();
        if (byStatus != null) {
            byStatus.forEach((estado, cantidad) -> porEstado.merge(ESTADOS.getOrDefault(estado, estado), cantidad, Long::sum));
        }
        List<ConteoDTO> porZona = new ArrayList<>();
        if (byNeighborhood != null) {
            byNeighborhood.forEach((barrio, cantidad) -> porZona.add(new ConteoDTO(barrio, cantidad)));
        }
        return new EstadisticasServiciosDTO(porEstado, List.of(), List.of(), List.of(), porZona, null, List.of(),
                porFranja());
    }

    private List<ConteoDTO> porFranja() {
        long[] cantidades = new long[FRANJAS.size()];
        if (byHour != null) {
            byHour.forEach((hora, cantidad) -> {
                if (hora != null && hora >= 0 && hora < 24) {
                    cantidades[hora / 6] += cantidad;
                }
            });
        }
        List<ConteoDTO> conteos = new ArrayList<>();
        for (int i = 0; i < FRANJAS.size(); i++) {
            conteos.add(new ConteoDTO(FRANJAS.get(i), cantidades[i]));
        }
        return conteos;
    }
}
