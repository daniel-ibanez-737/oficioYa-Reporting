package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;
import java.util.Map;

/**
 * Lo que Service (Squad 3) entrega vía Orchestrator para un periodo.
 * SUPUESTO: contrato pendiente de acordar con el Squad 3.
 */
public record EstadisticasServiciosDTO(
        Map<String, Long> solicitudesPorEstado,
        List<ConteoDTO> cumplidosPorDia,
        List<ConteoDTO> cumplidosPorOficio) {
}
