package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;

import edu.eci.dosw.oficioya.reporting.domain.TipoEstadistica;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoReporte;

/**
 * Entrada del catálogo de reportes disponibles.
 */
public record TipoReporteResponse(TipoReporte tipo, String titulo, String descripcion,
                                  List<TipoIndicador> indicadores, List<TipoEstadistica> estadisticas) {

    public static TipoReporteResponse desde(TipoReporte tipo) {
        return new TipoReporteResponse(tipo, tipo.titulo(), tipo.descripcion(), tipo.indicadores(),
                tipo.estadisticas());
    }
}
