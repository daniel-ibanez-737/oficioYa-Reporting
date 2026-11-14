package edu.eci.dosw.oficioya.reporting.service.exportacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.eci.dosw.oficioya.reporting.domain.Estadistica;
import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.domain.PuntoEstadistico;
import edu.eci.dosw.oficioya.reporting.domain.Reporte;
import edu.eci.dosw.oficioya.reporting.domain.TipoEstadistica;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoReporte;
import edu.eci.dosw.oficioya.reporting.dto.ArchivoExportado;
import edu.eci.dosw.oficioya.reporting.exception.SolicitudInvalidaException;

class ExportacionTest {

    private final ExportadorCsv csv = new ExportadorCsv();

    private static Reporte reporte() {
        PeriodoConsulta periodo = new PeriodoConsulta(LocalDate.of(2026, 10, 4), LocalDate.of(2026, 10, 10));
        return new Reporte(TipoReporte.SERVICIOS, periodo, LocalDateTime.of(2026, 10, 10, 14, 32),
                List.of(new Indicador(TipoIndicador.SERVICIOS_CUMPLIDOS, 1248, 1114.0, "servicios", "vs. 1114"),
                        new Indicador(TipoIndicador.TASA_CANCELACION, 4.17, null, "%", "52 de 1248; total")),
                List.of(new Estadistica(TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_OFICIO,
                        List.of(new PuntoEstadistico("Plomería", 40), new PuntoEstadistico("=HYPERLINK(\"x\")", 1)))));
    }

    @Test
    void elCsvTieneLosMismosDatosQueElReporte() {
        String contenido = new String(csv.exportar(reporte()), StandardCharsets.UTF_8);

        assertThat(contenido).startsWith("﻿Reporte;Reporte de solicitudes y servicios\r\n");
        assertThat(contenido).contains(
                "Periodo;2026-10-04;2026-10-10",
                "Comparado con;2026-09-27;2026-10-03",
                "Indicador;Valor;Periodo anterior;Variación %;Unidad;Detalle",
                "Total de servicios cumplidos;1248;1114;12;servicios;vs. 1114",
                // decimal con coma, sin valor anterior, y texto con separador entre comillas
                "Tasa de cancelación de solicitudes;4,17;;;%;\"52 de 1248; total\"",
                "Servicios cumplidos por oficio",
                "Plomería;40",
                "Total;41");
    }

    @Test
    void evitaInyeccionDeFormulasEnExcel() {
        String contenido = new String(csv.exportar(reporte()), StandardCharsets.UTF_8);
        assertThat(contenido).contains("\"'=HYPERLINK(\"\"x\"\")\";1");
    }

    @Test
    void escaparCampos() {
        assertThat(ExportadorCsv.escapar(null)).isEmpty();
        assertThat(ExportadorCsv.escapar("normal")).isEqualTo("normal");
        assertThat(ExportadorCsv.escapar("-5")).isEqualTo("-5");
        assertThat(ExportadorCsv.escapar("+57 300")).isEqualTo("'+57 300");
        assertThat(ExportadorCsv.escapar("línea\nnueva")).isEqualTo("\"línea\nnueva\"");
        assertThat(ExportadorCsv.numero(null)).isEmpty();
        assertThat(ExportadorCsv.numero(2.5)).isEqualTo("2,5");
    }

    @Test
    void laFabricaEntregaElExportadorDelFormato() {
        assertThat(new ExportadorFactory(List.of(csv)).obtener(FormatoExportacion.CSV)).isSameAs(csv);
        assertThat(csv.tipoContenido()).startsWith("text/csv");
        ExportadorFactory sinExportadores = new ExportadorFactory(List.of());
        assertThatThrownBy(() -> sinExportadores.obtener(FormatoExportacion.CSV))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void dosArchivosConLosMismosBytesSonIguales() {
        ArchivoExportado a = new ArchivoExportado("r.csv", "text/csv", new byte[] {1, 2, 3});
        ArchivoExportado b = new ArchivoExportado("r.csv", "text/csv", new byte[] {1, 2, 3});
        ArchivoExportado distinto = new ArchivoExportado("r.csv", "text/csv", new byte[] {9});

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(distinto).isNotEqualTo("r.csv");
        assertThat(a.toString()).contains("r.csv", "bytes=3");
        assertThat(new ArchivoExportado("v.csv", "text/csv", null).toString()).contains("bytes=0");
    }
}
