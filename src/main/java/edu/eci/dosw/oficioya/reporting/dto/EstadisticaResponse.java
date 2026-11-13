package edu.eci.dosw.oficioya.reporting.dto;

import java.util.List;

import edu.eci.dosw.oficioya.reporting.domain.Estadistica;
import edu.eci.dosw.oficioya.reporting.domain.PuntoEstadistico;
import edu.eci.dosw.oficioya.reporting.domain.TipoEstadistica;

public record EstadisticaResponse(TipoEstadistica tipo, String nombre, String descripcion, double total,
                                  String etiquetaMaxima, List<PuntoEstadistico> puntos) {

    public static EstadisticaResponse desde(Estadistica e) {
        return new EstadisticaResponse(e.tipo(), e.nombre(), e.descripcion(), e.total(),
                e.maximo().map(PuntoEstadistico::etiqueta).orElse(null), e.puntos());
    }
}
