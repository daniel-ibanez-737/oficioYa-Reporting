package edu.eci.dosw.oficioya.reporting.domain.indicador;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Reseñas reportadas en el periodo, con cuántos reportes siguen pendientes y cuántos se resolvieron.
 */
@Component
public class CalculadorResenasReportadas implements CalculadorIndicador {

    @Override
    public TipoIndicador tipo() {
        return TipoIndicador.RESENAS_REPORTADAS;
    }

    @Override
    public Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior) {
        return new Indicador(tipo(), actual.resenasReportadas(), (double) anterior.resenasReportadas(), "reseñas",
                actual.reportesPendientes() + " pendientes · " + actual.reportesResueltos() + " resueltas");
    }
}
