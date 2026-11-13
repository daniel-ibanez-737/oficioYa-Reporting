package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Trabajadores pausados hoy (estado actual, sin comparación), separados en voluntarios y por revisión.
 */
@Component
public class CalculadorTrabajadoresPausados implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.TRABAJADORES_PAUSADOS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.trabajadoresPausados(), null, "trabajadores",
                actual.pausadosVoluntarios() + " voluntarios · " + actual.pausadosPorReputacion() + " por revisión");
    }
}
