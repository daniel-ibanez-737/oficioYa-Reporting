package edu.eci.dosw.oficioya.reporting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import edu.eci.dosw.oficioya.reporting.domain.Reporte;
import edu.eci.dosw.oficioya.reporting.domain.TipoReporte;

public record ReporteResponse(TipoReporte tipo, String titulo, LocalDate desde, LocalDate hasta,
                              LocalDate desdeComparacion, LocalDate hastaComparacion, LocalDateTime generadoEn,
                              List<IndicadorResponse> indicadores, List<EstadisticaResponse> estadisticas) {

    public static ReporteResponse desde(Reporte r) {
        return new ReporteResponse(r.tipo(), r.titulo(), r.periodo().desde(), r.periodo().hasta(),
                r.periodo().periodoAnterior().desde(), r.periodo().periodoAnterior().hasta(), r.generadoEn(),
                r.indicadores().stream().map(IndicadorResponse::desde).toList(),
                r.estadisticas().stream().map(EstadisticaResponse::desde).toList());
    }
}
