package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Promedio diario de usuarios activos en el periodo, comparado con el periodo anterior.
 */
@Component
public class CalculadorPromedioUsuariosActivos implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.PROMEDIO_USUARIOS_ACTIVOS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.demanda().promedioUsuariosActivos(),
                anterior.demanda().promedioUsuariosActivos(), "usuarios por día", "Promedio diario en el periodo");
    }
}
