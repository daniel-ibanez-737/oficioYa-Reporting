package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;

/**
 * Lo que Identity / Usuarios entrega vía Orchestrator para un periodo: estado actual de las cuentas y usuarios que
 * tuvieron actividad (inicio de sesión) cada día. Solo conteos, ningún dato personal.
 * SUPUESTO: contrato pendiente de acordar con el squad de Identity / Usuarios.
 */
public record EstadisticasUsuariosDTO(
        long cuentasActivas,
        long cuentasInactivas,
        List<ConteoDTO> activosPorDia) {
}
