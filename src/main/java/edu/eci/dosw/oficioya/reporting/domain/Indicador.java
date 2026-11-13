package edu.eci.dosw.oficioya.reporting.domain;

/**
 * Indicador calculado para un periodo, comparado con el periodo anterior cuando aplica.
 *
 * @param valorAnterior null si el indicador es un estado actual y no se compara (por ejemplo, pausados)
 */
public record Indicador(TipoIndicador tipo, double valor, Double valorAnterior, String unidad, String detalle) {

    public String nombre() {
        return tipo.nombre();
    }

    /** Variación porcentual frente al periodo anterior, con un decimal; null si no hay base de comparación. */
    public Double variacionPorcentual() {
        if (valorAnterior == null || valorAnterior == 0) {
            return null;
        }
        return Math.round((valor - valorAnterior) / valorAnterior * 1000) / 10.0;
    }
}
