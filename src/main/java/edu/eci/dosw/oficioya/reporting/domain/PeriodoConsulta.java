package edu.eci.dosw.oficioya.reporting.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import edu.eci.dosw.oficioya.reporting.exception.SolicitudInvalidaException;

/**
 * Rango de fechas (ambas inclusive) sobre el que se consulta un reporte, indicador o estadística.
 */
public record PeriodoConsulta(LocalDate desde, LocalDate hasta) {

    public static final int MAXIMO_DIAS = 366;

    public PeriodoConsulta {
        if (desde == null || hasta == null) {
            throw new SolicitudInvalidaException("El periodo necesita fecha de inicio y de fin");
        }
        if (desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (ChronoUnit.DAYS.between(desde, hasta) + 1 > MAXIMO_DIAS) {
            throw new SolicitudInvalidaException("El periodo no puede superar " + MAXIMO_DIAS + " días");
        }
    }

    /**
     * Construye el periodo pedido. Sin fechas, toma los últimos {@code diasPorDefecto} días hasta hoy
     * (como la vista semanal del mockup). Con una sola fecha, la petición es inválida.
     */
    public static PeriodoConsulta de(LocalDate desde, LocalDate hasta, LocalDate hoy, int diasPorDefecto) {
        if (desde == null && hasta == null) {
            return new PeriodoConsulta(hoy.minusDays(diasPorDefecto - 1L), hoy);
        }
        return new PeriodoConsulta(desde, hasta);
    }

    public long dias() {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    /** Periodo de la misma duración inmediatamente anterior, para comparar indicadores. */
    public PeriodoConsulta periodoAnterior() {
        return new PeriodoConsulta(desde.minusDays(dias()), desde.minusDays(1));
    }

    public List<LocalDate> fechas() {
        return desde.datesUntil(hasta.plusDays(1)).toList();
    }
}
