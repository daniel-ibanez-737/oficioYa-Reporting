package edu.eci.dosw.oficioya.reporting.dto;

import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

public record IndicadorResponse(TipoIndicador tipo, String nombre, double valor, Double valorAnterior,
                                Double variacionPorcentual, String unidad, String detalle) {

    public static IndicadorResponse desde(Indicador i) {
        return new IndicadorResponse(i.tipo(), i.nombre(), i.valor(), i.valorAnterior(), i.variacionPorcentual(),
                i.unidad(), i.detalle());
    }
}
