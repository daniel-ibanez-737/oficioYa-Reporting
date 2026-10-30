package edu.eci.dosw.oficioya.reporting.client;

import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasReputacionDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasServiciosDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasTrabajadoresDTO;

/**
 * Única salida de Reporting: toda la información se pide a los otros dominios a través del Orchestrator.
 * Reporting solo recibe datos agregados (conteos y promedios), nunca datos personales.
 */
public interface OrquestadorClient {

    /** Service: solicitudes por estado y servicios cumplidos por día y por oficio. */
    EstadisticasServiciosDTO estadisticasServicios(PeriodoConsulta periodo);

    /** Worker: estado actual de los trabajadores (activos y pausados). */
    EstadisticasTrabajadoresDTO estadisticasTrabajadores();

    /** Reputation: calificaciones y reportes de reseñas del periodo. */
    EstadisticasReputacionDTO estadisticasReputacion(PeriodoConsulta periodo);
}
