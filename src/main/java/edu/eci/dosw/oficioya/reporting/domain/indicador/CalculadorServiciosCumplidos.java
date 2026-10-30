package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Servicios cumplidos en el periodo, comparados con el periodo anterior.
 */
@Component
public class CalculadorServiciosCumplidos implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.SERVICIOS_CUMPLIDOS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.serviciosCumplidos(), (double) anterior.serviciosCumplidos(), "servicios",
                "vs. " + anterior.serviciosCumplidos() + " el periodo anterior");
    }
}
