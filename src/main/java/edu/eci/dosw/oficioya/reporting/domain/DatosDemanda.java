package edu.eci.dosw.oficioya.reporting.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Datos de cuentas, uso y demanda de un periodo (review del Sprint 4), traídos de Identity / Usuarios y de Service.
 * Solo conteos y promedios: ningún dato personal (RNF-05).
 */
public record DatosDemanda(
        long cuentasActivas,
        long cuentasInactivas,
        List<PuntoEstadistico> usuariosActivosPorDia,
        List<PuntoEstadistico> solicitudesPorOficio,
        List<PuntoEstadistico> solicitudesPorZona,
        double tarifaPromedio,
        List<PuntoEstadistico> tarifaPromedioPorOficio,
        List<PuntoEstadistico> solicitudesPorFranja) {

    private static final Comparator<PuntoEstadistico> DE_MAYOR_A_MENOR =
            Comparator.comparingDouble(PuntoEstadistico::valor).reversed().thenComparing(PuntoEstadistico::etiqueta);

    // debe ir después del comparador: el constructor lo usa
    public static final DatosDemanda VACIOS = new DatosDemanda(0, 0, List.of(), List.of(), List.of(), 0, List.of(), List.of());

    public DatosDemanda {
        usuariosActivosPorDia = List.copyOf(usuariosActivosPorDia);
        solicitudesPorOficio = solicitudesPorOficio.stream().sorted(DE_MAYOR_A_MENOR).toList();
        solicitudesPorZona = solicitudesPorZona.stream().sorted(DE_MAYOR_A_MENOR).toList();
        tarifaPromedioPorOficio = tarifaPromedioPorOficio.stream().sorted(DE_MAYOR_A_MENOR).toList();
        solicitudesPorFranja = solicitudesPorFranja.stream().sorted(DE_MAYOR_A_MENOR).toList();
    }

    /** Promedio diario de usuarios activos en el periodo, con un decimal; 0 si no hay datos. */
    public double promedioUsuariosActivos() {
        double promedio = usuariosActivosPorDia.stream().mapToDouble(PuntoEstadistico::valor).average().orElse(0);
        return Math.round(promedio * 10) / 10.0;
    }

    /** Oficio con más solicitudes en el periodo (en empate, el primero en orden alfabético). */
    public Optional<PuntoEstadistico> categoriaMasApetecida() {
        return solicitudesPorOficio.stream().filter(p -> p.valor() > 0).findFirst();
    }
}
