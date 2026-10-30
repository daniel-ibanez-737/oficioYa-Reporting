package edu.eci.dosw.oficioya.reporting.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reporte generado bajo demanda. No se guarda (Reporting no tiene almacenamiento propio): lo que se consulta
 * y lo que se exporta salen del mismo objeto, por eso son consistentes (RNF-06).
 */
public record Reporte(TipoReporte tipo, PeriodoConsulta periodo, LocalDateTime generadoEn,
                      List<Indicador> indicadores, List<Estadistica> estadisticas) {

    public Reporte {
        indicadores = List.copyOf(indicadores);
        estadisticas = List.copyOf(estadisticas);
    }

    public String titulo() {
        return tipo.titulo();
    }
}
