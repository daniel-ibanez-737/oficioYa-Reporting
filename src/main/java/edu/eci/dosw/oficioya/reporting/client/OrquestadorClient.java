package edu.eci.dosw.oficioya.reporting.client;

import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasReputacionDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasServiciosDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasTrabajadoresDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasUsuariosDTO;

/**
 * Única salida de Reporting: toda la información se pide a los otros dominios a través del Orchestrator.
 * Reporting solo recibe datos agregados (conteos y promedios), nunca datos personales.
 */
public interface OrquestadorClient {

    /** Service: solicitudes por estado, oficio y zona, servicios cumplidos por día y oficio, y tarifas. */
    EstadisticasServiciosDTO estadisticasServicios(PeriodoConsulta periodo);

    /** Worker: estado actual de los trabajadores (activos y pausados). */
    EstadisticasTrabajadoresDTO estadisticasTrabajadores();

    /** Reputation: calificaciones y reportes de reseñas del periodo. */
    EstadisticasReputacionDTO estadisticasReputacion(PeriodoConsulta periodo);

    /** Identity / Usuarios: cuentas activas e inactivas y usuarios activos por día del periodo. */
    EstadisticasUsuariosDTO estadisticasUsuarios(PeriodoConsulta periodo);
}
