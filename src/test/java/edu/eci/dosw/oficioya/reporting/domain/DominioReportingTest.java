package edu.eci.dosw.oficioya.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCalificacionPromedio;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorResenasReportadas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorServiciosCumplidos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTasaCancelacion;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTrabajadoresActivos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTrabajadoresPausados;
import edu.eci.dosw.oficioya.reporting.exception.SolicitudInvalidaException;

class DominioReportingTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 10);
    private static final PeriodoConsulta SEMANA = new PeriodoConsulta(LocalDate.of(2026, 10, 4), HOY);

    private static DatosPlataforma datos(long cumplidos, long cancelados, long reportadas, double calificacion) {
        return new DatosPlataforma(SEMANA,
                Map.of("CUMPLIDA", cumplidos, "CANCELADA", cancelados, "EN_CURSO", 10L),
                List.of(new PuntoEstadistico("2026-10-04", 142), new PuntoEstadistico("2026-10-05", 168)),
                List.of(new PuntoEstadistico("Plomería", 40)),
                400, 12, 6,
                List.of(new PuntoEstadistico("Plomería", 120)),
                calificacion, reportadas, 5, 2);
    }

    // ---------- Periodo ----------

    @Test
    void periodoPorDefectoSonLosUltimosSieteDias() {
        PeriodoConsulta periodo = PeriodoConsulta.de(null, null, HOY, 7);
        assertThat(periodo).isEqualTo(SEMANA);
        assertThat(periodo.dias()).isEqualTo(7);
        assertThat(periodo.fechas()).hasSize(7).startsWith(LocalDate.of(2026, 10, 4)).endsWith(HOY);
    }

    @Test
    void periodoAnteriorTieneLaMismaDuracion() {
        assertThat(SEMANA.periodoAnterior())
                .isEqualTo(new PeriodoConsulta(LocalDate.of(2026, 9, 27), LocalDate.of(2026, 10, 3)));
    }

    @Test
    void periodosInvalidos() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        assertThatThrownBy(() -> PeriodoConsulta.de(inicio, null, HOY, 7)).isInstanceOf(SolicitudInvalidaException.class);
        assertThatThrownBy(() -> new PeriodoConsulta(HOY, inicio)).isInstanceOf(SolicitudInvalidaException.class);
        LocalDate muyLejos = inicio.plusDays(PeriodoConsulta.MAXIMO_DIAS);
        assertThatThrownBy(() -> new PeriodoConsulta(inicio, muyLejos)).isInstanceOf(SolicitudInvalidaException.class);
        assertThat(new PeriodoConsulta(inicio, muyLejos.minusDays(1)).dias()).isEqualTo(PeriodoConsulta.MAXIMO_DIAS);
    }

    // ---------- Indicadores y estadísticas ----------

    @Test
    void variacionPorcentualFrenteAlPeriodoAnterior() {
        assertThat(new Indicador(TipoIndicador.SERVICIOS_CUMPLIDOS, 1248, 1114.0, "", "").variacionPorcentual())
                .isEqualTo(12.0);
        assertThat(new Indicador(TipoIndicador.SERVICIOS_CUMPLIDOS, 5, 0.0, "", "").variacionPorcentual()).isNull();
        assertThat(new Indicador(TipoIndicador.TRABAJADORES_ACTIVOS, 5, null, "", "").variacionPorcentual()).isNull();
    }

    @Test
    void estadisticaCalculaTotalYMaximo() {
        Estadistica e = datos(1, 0, 0, 0).estadistica(TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_DIA);
        assertThat(e.total()).isEqualTo(310);
        assertThat(e.maximo()).map(PuntoEstadistico::etiqueta).contains("2026-10-05");
        assertThat(e.nombre()).isEqualTo("Servicios cumplidos por día");
        assertThat(new Estadistica(TipoEstadistica.TRABAJADORES_POR_OFICIO, List.of()).maximo()).isEmpty();
    }

    @Test
    void cadaTipoDeEstadisticaSeArmaConLosDatosDelPeriodo() {
        DatosPlataforma d = datos(100, 5, 7, 4.5);
        assertThat(d.estadistica(TipoEstadistica.SERVICIOS_CUMPLIDOS_POR_OFICIO).total()).isEqualTo(40);
        assertThat(d.estadistica(TipoEstadistica.SOLICITUDES_POR_ESTADO).puntos())
                .extracting(PuntoEstadistico::etiqueta).containsExactly("CANCELADA", "CUMPLIDA", "EN_CURSO");
        assertThat(d.estadistica(TipoEstadistica.TRABAJADORES_POR_OFICIO).total()).isEqualTo(120);
        assertThat(d.estadistica(TipoEstadistica.REPORTES_DE_RESENAS_POR_ESTADO).puntos())
                .containsExactly(new PuntoEstadistico("Pendientes", 5), new PuntoEstadistico("Resueltos", 2));
        assertThat(d.totalSolicitudes()).isEqualTo(115);
        assertThat(d.trabajadoresPausados()).isEqualTo(18);
    }

    @Test
    void calculadoresDeIndicadores() {
        DatosPlataforma actual = datos(1248, 52, 7, 4.6);
        DatosPlataforma anterior = datos(1114, 40, 5, 4.4);

        Indicador cumplidos = new CalculadorServiciosCumplidos().calcular(actual, anterior);
        assertThat(cumplidos.valor()).isEqualTo(1248);
        assertThat(cumplidos.variacionPorcentual()).isEqualTo(12.0);
        assertThat(cumplidos.detalle()).isEqualTo("vs. 1114 el periodo anterior");

        Indicador pausados = new CalculadorTrabajadoresPausados().calcular(actual, anterior);
        assertThat(pausados.valor()).isEqualTo(18);
        assertThat(pausados.valorAnterior()).isNull();
        assertThat(pausados.detalle()).isEqualTo("12 voluntarios · 6 por revisión");

        Indicador reportadas = new CalculadorResenasReportadas().calcular(actual, anterior);
        assertThat(reportadas.valor()).isEqualTo(7);
        assertThat(reportadas.detalle()).isEqualTo("5 pendientes · 2 resueltas");

        assertThat(new CalculadorTasaCancelacion().calcular(actual, anterior).valor()).isEqualTo(4.0);
        assertThat(new CalculadorTrabajadoresActivos().calcular(actual, anterior).valor()).isEqualTo(400);
        assertThat(new CalculadorCalificacionPromedio().calcular(actual, anterior).valorAnterior()).isEqualTo(4.4);
    }

    @Test
    void tasaDeCancelacionSinSolicitudesEsCero() {
        DatosPlataforma vacio = new DatosPlataforma(SEMANA, Map.of(), List.of(), List.of(), 0, 0, 0, List.of(), 0, 0, 0, 0);
        assertThat(new CalculadorTasaCancelacion().calcular(vacio, vacio).valor()).isZero();
        assertThat(vacio.serviciosCumplidos()).isZero();
    }

    @Test
    void elReporteGeneralIncluyeTodo() {
        assertThat(TipoReporte.GENERAL.indicadores()).hasSize(TipoIndicador.values().length);
        assertThat(TipoReporte.GENERAL.estadisticas()).hasSize(TipoEstadistica.values().length);
        assertThat(TipoReporte.SERVICIOS.titulo()).isNotBlank();
    }
}
