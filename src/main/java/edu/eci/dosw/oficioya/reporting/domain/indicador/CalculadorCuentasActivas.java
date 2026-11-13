package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Cuentas activas hoy (estado actual, sin comparación). Dato de Identity / Usuarios.
 */
@Component
public class CalculadorCuentasActivas implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.CUENTAS_ACTIVAS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.demanda().cuentasActivas(), null, "cuentas", "Estado actual");
    }
}
