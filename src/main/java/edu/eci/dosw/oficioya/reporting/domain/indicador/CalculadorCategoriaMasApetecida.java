package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Oficio con más solicitudes en el periodo. El valor es su número de solicitudes y el detalle, el oficio.
 */
@Component
public class CalculadorCategoriaMasApetecida implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.CATEGORIA_MAS_APETECIDA;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return actual.demanda().categoriaMasApetecida()
                .map(p -> new Indicador(tipo(), p.valor(), null, "solicitudes", p.etiqueta()))
                .orElseGet(() -> new Indicador(tipo(), 0, null, "solicitudes", "Sin solicitudes en el periodo"));
    }
}
