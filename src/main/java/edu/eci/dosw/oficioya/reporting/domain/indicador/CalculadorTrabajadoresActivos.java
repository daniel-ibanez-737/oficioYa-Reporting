package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Trabajadores activos hoy (estado actual, sin comparación).
 */
@Component
public class CalculadorTrabajadoresActivos implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.TRABAJADORES_ACTIVOS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.trabajadoresActivos(), null, "trabajadores", "Estado actual");
    }
}
