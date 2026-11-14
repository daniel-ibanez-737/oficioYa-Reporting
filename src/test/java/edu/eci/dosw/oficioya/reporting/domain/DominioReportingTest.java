package edu.eci.dosw.oficioya.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCalificacionPromedio;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCategoriaMasApetecida;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCuentasActivas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorCuentasInactivas;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorPromedioUsuariosActivos;
import edu.eci.dosw.oficioya.reporting.domain.indicador.CalculadorTarifaPromedio;
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
                Map.of("CUMPLIDA", cumplidos, "CANCELADA", cancelados, "ACEPTADA", 10L),
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
                .extracting(PuntoEstadistico::etiqueta).containsExactly("ACEPTADA", "CUMPLIDA", "CANCELADA");
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

    // ---------- Review del Sprint 4: cuentas, usuarios activos, demanda y tarifas ----------

    private static DatosPlataforma conDemanda(DatosDemanda demanda) {
        return new DatosPlataforma(SEMANA, Map.of("ESPERANDO_PAGO", 3L, "CUMPLIDA", 10L, "PENDIENTE", 4L,
                "ACEPTADA", 2L), List.of(), List.of(), 0, 0, 0, List.of(), 0, 0, 0, 0, demanda);
    }

    private static final DatosDemanda DEMANDA = new DatosDemanda(1480, 236,
            List.of(new PuntoEstadistico("2026-10-04", 600), new PuntoEstadistico("2026-10-05", 641)),
            List.of(new PuntoEstadistico("Electricidad", 90), new PuntoEstadistico("Plomería", 120),
                    new PuntoEstadistico("Carpintería", 90)),
            List.of(new PuntoEstadistico("Suba", 40), new PuntoEstadistico("Chapinero", 75)),
            62_450.4,
            List.of(new PuntoEstadistico("Plomería", 85_000), new PuntoEstadistico("Carpintería", 95_000)),
            List.of(new PuntoEstadistico("Noche", 20), new PuntoEstadistico("Mañana", 60)));

    @Test
    void elFlujoDeTrabajoSigueLasEtapasDeLaSolicitud() {
        assertThat(conDemanda(DEMANDA).estadistica(TipoEstadistica.SOLICITUDES_POR_ESTADO).puntos())
                .extracting(PuntoEstadistico::etiqueta)
                .containsExactly("PENDIENTE", "ACEPTADA", "CUMPLIDA", "ESPERANDO_PAGO");
    }

    @Test
    void estadisticasDeCuentasDemandaYTarifas() {
        DatosPlataforma d = conDemanda(DEMANDA);
        assertThat(d.estadistica(TipoEstadistica.CUENTAS_POR_ESTADO).puntos())
                .containsExactly(new PuntoEstadistico("Activas", 1480), new PuntoEstadistico("Inactivas", 236));
        assertThat(d.estadistica(TipoEstadistica.USUARIOS_ACTIVOS_POR_DIA).total()).isEqualTo(1241);
        // de mayor a menor y, en empate, por nombre
        assertThat(d.estadistica(TipoEstadistica.SOLICITUDES_POR_OFICIO).puntos()).extracting(PuntoEstadistico::etiqueta)
                .containsExactly("Plomería", "Carpintería", "Electricidad");
        assertThat(d.estadistica(TipoEstadistica.SOLICITUDES_POR_ZONA).puntos()).extracting(PuntoEstadistico::etiqueta)
                .containsExactly("Chapinero", "Suba");
        assertThat(d.estadistica(TipoEstadistica.SOLICITUDES_POR_FRANJA).puntos()).extracting(PuntoEstadistico::etiqueta)
                .containsExactly("Mañana", "Noche");
        assertThat(d.estadistica(TipoEstadistica.TARIFA_PROMEDIO_POR_OFICIO).maximo())
                .contains(new PuntoEstadistico("Carpintería", 95_000));
        assertThat(DEMANDA.promedioUsuariosActivos()).isEqualTo(620.5);
    }

    @Test
    void calculadoresDeCuentasDemandaYTarifas() {
        DatosPlataforma actual = conDemanda(DEMANDA);
        DatosPlataforma anterior = conDemanda(new DatosDemanda(1400, 250,
                List.of(new PuntoEstadistico("2026-09-27", 500)), List.of(), List.of(), 60_000, List.of(), List.of()));

        Indicador activas = new CalculadorCuentasActivas().calcular(actual, anterior);
        assertThat(activas.valor()).isEqualTo(1480);
        assertThat(activas.valorAnterior()).isNull();
        assertThat(new CalculadorCuentasInactivas().calcular(actual, anterior).valor()).isEqualTo(236);

        Indicador usuarios = new CalculadorPromedioUsuariosActivos().calcular(actual, anterior);
        assertThat(usuarios.valor()).isEqualTo(620.5);
        assertThat(usuarios.valorAnterior()).isEqualTo(500);
        assertThat(usuarios.variacionPorcentual()).isEqualTo(24.1);

        Indicador categoria = new CalculadorCategoriaMasApetecida().calcular(actual, anterior);
        assertThat(categoria.valor()).isEqualTo(120);
        assertThat(categoria.detalle()).isEqualTo("Plomería");
        assertThat(new CalculadorCategoriaMasApetecida().calcular(anterior, anterior).detalle())
                .isEqualTo("Sin solicitudes en el periodo");

        Indicador tarifa = new CalculadorTarifaPromedio().calcular(actual, anterior);
        assertThat(tarifa.valor()).isEqualTo(62_450);
        assertThat(tarifa.valorAnterior()).isEqualTo(60_000);
        assertThat(tarifa.unidad()).isEqualTo("COP");
    }

    @Test
    void sinDatosDeDemandaTodoQuedaEnCero() {
        DatosPlataforma sinDemanda = conDemanda(null);
        assertThat(sinDemanda.demanda()).isEqualTo(DatosDemanda.VACIOS);
        assertThat(DatosDemanda.VACIOS.promedioUsuariosActivos()).isZero();
        assertThat(DatosDemanda.VACIOS.categoriaMasApetecida()).isEmpty();
        assertThat(new CalculadorTarifaPromedio().calcular(sinDemanda, sinDemanda).valor()).isZero();
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
        assertThat(TipoReporte.USUARIOS.indicadores()).containsExactly(TipoIndicador.CUENTAS_ACTIVAS,
                TipoIndicador.CUENTAS_INACTIVAS, TipoIndicador.PROMEDIO_USUARIOS_ACTIVOS);
        assertThat(TipoEstadistica.SOLICITUDES_POR_ESTADO.nombre()).isEqualTo("Flujo de trabajo");
    }
}
