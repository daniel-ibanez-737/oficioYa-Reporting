package edu.eci.dosw.oficioya.reporting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.eci.dosw.oficioya.reporting.client.OrquestadorClient;
import edu.eci.dosw.oficioya.reporting.domain.Estadistica;
import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.domain.PuntoEstadistico;
import edu.eci.dosw.oficioya.reporting.domain.Reporte;
import edu.eci.dosw.oficioya.reporting.domain.TipoEstadistica;
import edu.eci.dosw.oficioya.reporting.domain.TipoIndicador;
import edu.eci.dosw.oficioya.reporting.domain.TipoReporte;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCalificacionPromedio;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCategoriaMasApetecida;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCuentasActivas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCuentasInactivas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorPromedioUsuariosActivos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTarifaPromedio;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorIndicador;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorResenasReportadas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorServiciosCumplidos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTasaCancelacion;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTrabajadoresActivos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTrabajadoresPausados;
import edu.eci.dosw.oficioya.reporting.dto.ArchivoExportado;
import edu.eci.dosw.oficioya.reporting.dto.ConteoDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasReputacionDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasServiciosDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasTrabajadoresDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasUsuariosDTO;
import edu.eci.dosw.oficioya.reporting.service.exportacion.ExportadorCsv;
import edu.eci.dosw.oficioya.reporting.service.exportacion.ExportadorFactory;

class ReporteServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 10);
    private static final PeriodoConsulta SEMANA = new PeriodoConsulta(LocalDate.of(2026, 10, 4), HOY);
    private static final PeriodoConsulta ANTERIOR = SEMANA.periodoAnterior();
    private static final List<CalculadorIndicador> CALCULADORES = List.of(new CalculadorServiciosCumplidos(),
            new CalculadorTrabajadoresPausados(), new CalculadorResenasReportadas(), new CalculadorTasaCancelacion(),
            new CalculadorTrabajadoresActivos(), new CalculadorCalificacionPromedio(), new CalculadorCuentasActivas(),
            new CalculadorCuentasInactivas(), new CalculadorPromedioUsuariosActivos(),
            new CalculadorCategoriaMasApetecida(), new CalculadorTarifaPromedio());

    private OrquestadorClient orquestador;
    private ReporteService servicio;

    @BeforeEach
    void preparar() {
        orquestador = mock(OrquestadorClient.class);
        Clock reloj = Clock.fixed(HOY.atTime(14, 32).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        servicio = new ReporteService(orquestador, CALCULADORES,
                new ExportadorFactory(List.of(new ExportadorCsv())), reloj, 7);

        when(orquestador.estadisticasServicios(SEMANA)).thenReturn(new EstadisticasServiciosDTO(
                Map.of("CUMPLIDA", 1248L, "CANCELADA", 52L),
                // solo llegan dos días: los demás deben quedar en 0
                List.of(new ConteoDTO("2026-10-04", 142), new ConteoDTO("2026-10-09", 218)),
                List.of(new ConteoDTO("Plomería", 400)),
                List.of(new ConteoDTO("Electricidad", 300), new ConteoDTO("Plomería", 450)),
                List.of(new ConteoDTO("Suba", 200), new ConteoDTO("Chapinero", 380)),
                64_000.0,
                List.of(new ConteoDTO("Plomería", 85_000))));
        when(orquestador.estadisticasServicios(ANTERIOR)).thenReturn(new EstadisticasServiciosDTO(
                Map.of("CUMPLIDA", 1114L), null, null));
        when(orquestador.estadisticasTrabajadores()).thenReturn(new EstadisticasTrabajadoresDTO(400, 12, 6, null));
        when(orquestador.estadisticasReputacion(any())).thenReturn(new EstadisticasReputacionDTO(500, 4.6, 7, 5, 2));
        when(orquestador.estadisticasUsuarios(SEMANA)).thenReturn(new EstadisticasUsuariosDTO(1480, 236,
                List.of(new ConteoDTO("2026-10-04", 700))));
        when(orquestador.estadisticasUsuarios(ANTERIOR)).thenReturn(new EstadisticasUsuariosDTO(1400, 250, null));
    }

    @Test
    void generaElReporteDeServiciosConComparacion() {
        Reporte reporte = servicio.generar(TipoReporte.SERVICIOS, SEMANA, "admin-1");

        assertThat(reporte.indicadores()).extracting(Indicador::tipo)
                .containsExactly(TipoIndicador.SERVICIOS_CUMPLIDOS, TipoIndicador.TASA_CANCELACION,
                        TipoIndicador.CATEGORIA_MAS_APETECIDA, TipoIndicador.TARIFA_PROMEDIO);
        Indicador cumplidos = reporte.indicadores().get(0);
        assertThat(cumplidos.valor()).isEqualTo(1248);
        assertThat(cumplidos.variacionPorcentual()).isEqualTo(12.0);
        assertThat(reporte.generadoEn()).isEqualTo(HOY.atTime(14, 32));

        Estadistica porDia = reporte.estadisticas().get(0);
        assertThat(porDia.tipo()).isEqualTo(TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_DIA);
        assertThat(porDia.puntos()).hasSize(7).extracting(PuntoEstadistico::valor)
                .containsExactly(142.0, 0.0, 0.0, 0.0, 0.0, 218.0, 0.0);
    }

    @Test
    void generaLasMetricasDeDemandaYDeUsuarios() {
        Reporte servicios = servicio.generar(TipoReporte.SERVICIOS, SEMANA, "admin-1");
        Indicador categoria = servicios.indicadores().get(2);
        assertThat(categoria.tipo()).isEqualTo(TipoIndicador.CATEGORIA_MAS_APETECIDA);
        assertThat(categoria.detalle()).isEqualTo("Plomería");
        assertThat(categoria.valor()).isEqualTo(450);
        Indicador tarifa = servicios.indicadores().get(3);
        assertThat(tarifa.valor()).isEqualTo(64_000);
        assertThat(tarifa.valorAnterior()).isZero();
        assertThat(servicios.estadisticas()).filteredOn(e -> e.tipo() == TipoEstadistica.SOLICITUDES_POR_ZONA)
                .singleElement().satisfies(e -> assertThat(e.maximo()).contains(new PuntoEstadistico("Chapinero", 380)));

        Reporte usuarios = servicio.generar(TipoReporte.USUARIOS, SEMANA, "admin-1");
        assertThat(usuarios.indicadores()).extracting(Indicador::valor).containsExactly(1480.0, 236.0, 100.0);
        // un día con datos y seis en 0: el promedio es 700 / 7
        assertThat(usuarios.estadisticas().get(1).puntos()).hasSize(7);
    }

    @Test
    void indicadoresYEstadisticasDeLaPlataforma() {
        assertThat(servicio.indicadores(SEMANA, "admin-1")).hasSize(TipoIndicador.values().length);
        List<Estadistica> estadisticas = servicio.estadisticas(SEMANA, "admin-1");
        assertThat(estadisticas).hasSize(TipoEstadistica.values().length);
        // listas nulas de los otros dominios se tratan como vacías
        assertThat(estadisticas.get(3).puntos()).isEmpty();
    }

    @Test
    void sinSolicitudesPorEstadoNoFalla() {
        when(orquestador.estadisticasServicios(SEMANA)).thenReturn(new EstadisticasServiciosDTO(null, null, null));
        assertThat(servicio.generar(TipoReporte.SERVICIOS, SEMANA, "admin-1").indicadores().get(0).valor()).isZero();
    }

    @Test
    void exportaElMismoReporteEnCsv() {
        ArchivoExportado archivo = servicio.exportar(TipoReporte.SERVICIOS, SEMANA, FormatoExportacion.CSV, "admin-1");

        assertThat(archivo.nombre()).isEqualTo("reporte-servicios-2026-10-04_2026-10-10.csv");
        assertThat(archivo.tipoContenido()).startsWith("text/csv");
        assertThat(new String(archivo.contenido(), StandardCharsets.UTF_8))
                .contains("Total de servicios cumplidos;1248;1114;12;");
    }

    @Test
    void periodoPorDefectoYCatalogo() {
        assertThat(servicio.periodo(null, null)).isEqualTo(SEMANA);
        assertThat(servicio.periodo(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)).dias()).isEqualTo(30);
        assertThat(servicio.catalogo()).containsExactly(TipoReporte.values());
    }

    @Test
    void exigeUnCalculadorPorIndicador() {
        List<CalculadorIndicador> incompletos = List.of(new CalculadorServiciosCumplidos());
        ExportadorFactory fabrica = new ExportadorFactory(List.of());
        Clock reloj = Clock.systemUTC();
        assertThatThrownBy(() -> new ReporteService(orquestador, incompletos, fabrica, reloj, 7))
                .isInstanceOf(IllegalStateException.class);
    }
}
