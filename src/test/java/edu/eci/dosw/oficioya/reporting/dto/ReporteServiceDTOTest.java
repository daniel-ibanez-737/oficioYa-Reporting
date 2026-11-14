package edu.eci.dosw.oficioya.reporting.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ReporteServiceDTOTest {

    @Test
    void traduceLosEstadosYAgrupaLasHorasEnFranjasDeSeisHoras() {
        ReporteServiceDTO reporte = new ReporteServiceDTO(14,
                Map.of("PENDING", 3L, "COMPLETED", 5L, "DELETED", 1L, "CONFLICT", 2L, "OTRO", 3L),
                Map.of("Chapinero", 9L, "Suba", 5L),
                Map.of(2, 1L, 7, 4L, 11, 2L, 12, 3L, 23, 4L, 24, 9L));

        EstadisticasServiciosDTO estadisticas = reporte.aEstadisticas();

        assertThat(estadisticas.solicitudesPorEstado()).containsEntry("PENDIENTE", 3L).containsEntry("CUMPLIDA", 5L)
                .containsEntry("ELIMINADA", 1L).containsEntry("CONFLICTO", 2L).containsEntry("OTRO", 3L);
        assertThat(estadisticas.solicitudesPorZona()).containsExactlyInAnyOrder(new ConteoDTO("Chapinero", 9),
                new ConteoDTO("Suba", 5));
        // La hora 24 no existe y se ignora.
        assertThat(estadisticas.solicitudesPorFranja()).extracting(ConteoDTO::cantidad).containsExactly(1L, 6L, 3L, 4L);
    }

    @Test
    void sinDatosDevuelveListasVaciasYFranjasEnCero() {
        EstadisticasServiciosDTO estadisticas = new ReporteServiceDTO(0, null, null, null).aEstadisticas();

        assertThat(estadisticas.solicitudesPorEstado()).isEmpty();
        assertThat(estadisticas.solicitudesPorZona()).isEmpty();
        assertThat(estadisticas.solicitudesPorFranja()).extracting(ConteoDTO::cantidad).containsOnly(0L);
    }
}
