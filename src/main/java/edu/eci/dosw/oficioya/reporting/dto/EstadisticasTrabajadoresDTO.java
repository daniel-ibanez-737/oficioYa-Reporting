package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;

/**
 * Lo que Worker (Squad 4) entrega vía Orchestrator: estado actual de los trabajadores.
 * SUPUESTO: contrato pendiente de acordar con el Squad 4.
 */
public record EstadisticasTrabajadoresDTO(
        long activos,
        long pausadosVoluntarios,
        long pausadosPorReputacion,
        List<ConteoDTO> activosPorOficio) {
}
