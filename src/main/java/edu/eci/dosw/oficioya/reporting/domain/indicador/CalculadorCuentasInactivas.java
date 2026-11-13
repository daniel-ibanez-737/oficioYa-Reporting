package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Cuentas inactivas hoy (estado actual, sin comparación). Dato de Identity / Usuarios.
 */
@Component
public class CalculadorCuentasInactivas implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.CUENTAS_INACTIVAS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.demanda().cuentasInactivas(), null, "cuentas", "Estado actual");
    }
}
