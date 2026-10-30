package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Calificación promedio de las reseñas del periodo, comparada con el periodo anterior.
 */
@Component
public class CalculadorCalificacionPromedio implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.CALIFICACION_PROMEDIO;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.calificacionPromedio(), anterior.calificacionPromedio(), "estrellas",
                "Promedio de las reseñas registradas en el periodo");
    }
}
