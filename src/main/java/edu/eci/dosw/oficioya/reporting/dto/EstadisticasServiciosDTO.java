package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;
import java.util.Map;

/**
 * Lo que Service (Squad 3) entrega vía Orchestrator para un periodo.
 * SUPUESTO: contrato pendiente de acordar con el Squad 3. Los cuatro últimos campos se agregaron en la review del
 * Sprint 4 (categoría más apetecida, zonas más concurridas y tarifa promedio).
 *
 * @param solicitudesPorOficio    solicitudes creadas en el periodo por oficio (categoría)
 * @param solicitudesPorZona      solicitudes creadas en el periodo por zona (barrio o localidad)
 * @param tarifaPromedio          tarifa promedio (COP) de las solicitudes del periodo
 * @param tarifaPromedioPorOficio tarifa promedio (COP) de las solicitudes del periodo por oficio
 */
public record EstadisticasServiciosDTO(
        Map<String, Long> solicitudesPorEstado,
        List<ConteoDTO> cumplidosPorDia,
        List<ConteoDTO> cumplidosPorOficio,
        List<ConteoDTO> solicitudesPorOficio,
        List<ConteoDTO> solicitudesPorZona,
        Double tarifaPromedio,
        List<ConteoDTO> tarifaPromedioPorOficio) {

    /** Respuesta sin los datos de demanda ni de tarifas (versión anterior del contrato). */
    public EstadisticasServiciosDTO(Map<String, Long> solicitudesPorEstado, List<ConteoDTO> cumplidosPorDia,
                                    List<ConteoDTO> cumplidosPorOficio) {
        this(solicitudesPorEstado, cumplidosPorDia, cumplidosPorOficio, null, null, null, null);
    }
}
