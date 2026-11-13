package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Tarifa promedio (COP) de las solicitudes del periodo, comparada con el periodo anterior.
 */
@Component
public class CalculadorTarifaPromedio implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.TARIFA_PROMEDIO;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), Math.round(actual.demanda().tarifaPromedio()),
                (double) Math.round(anterior.demanda().tarifaPromedio()), "COP",
                "Promedio de las solicitudes del periodo");
    }
}
