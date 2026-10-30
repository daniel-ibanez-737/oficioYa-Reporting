package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Porcentaje de solicitudes del periodo que terminaron canceladas.
 */
@Component
public class CalculadorTasaCancelacion implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.TASA_CANCELACION;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), tasa(actual), tasa(anterior), "%",
                actual.solicitudesCanceladas() + " de " + actual.totalSolicitudes() + " solicitudes");
    }

    static double tasa(DatosPlataforma datos) {
        if (datos.totalSolicitudes() == 0) {
            return 0;
        }
        return Math.round(datos.solicitudesCanceladas() * 1000.0 / datos.totalSolicitudes()) / 10.0;
    }
}
