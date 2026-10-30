package edu.eci.dosw.oficioya.reporting.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Serie de valores agregados (por día, por oficio, por estado...).
 */
public record Estadistica(TipoEstadistica tipo, List<PuntoEstadistico> puntos) {

    public Estadistica {
        puntos = List.copyOf(puntos);
    }

    public String nombre() {
        return tipo.nombre();
    }

    public String descripcion() {
        return tipo.descripcion();
    }

    public double total() {
        return puntos.stream().mapToDouble(PuntoEstadistico::valor).sum();
    }

    public Optional<PuntoEstadistico> maximo() {
        return puntos.stream().max(Comparator.comparingDouble(PuntoEstadistico::valor));
    }
}
