package edu.eci.dosw.oficioya.reporting.domain.indicador;

import edu.eci.dosw.oficioya.reporting.domain.DatosPlataforma;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;

/**
 * Estrategia para calcular un indicador (RF-02). Cada indicador es una clase pequeña:
 * agregar uno nuevo no obliga a cambiar el servicio.
 */
public interface CalculadorIndicador {

    TipoIndicador tipo();

    /**
     * @param actual   datos del periodo consultado
     * @param anterior datos del periodo anterior de la misma duración, para comparar
     */
    Indicador calcular(DatosPlataforma actual, DatosPlataforma anterior);
}
