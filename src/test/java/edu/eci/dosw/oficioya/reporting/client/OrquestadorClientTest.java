package edu.eci.dosw.oficioya.reporting.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasServiciosDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasTrabajadoresDTO;
import edu.eci.dosw.oficioya.reporting.exception.IntegracionException;

class OrquestadorClientTest {

    private static final String BASE = "http://orquestador";
    private static final PeriodoConsulta SEMANA =
            new PeriodoConsulta(LocalDate.of(2026, 10, 4), LocalDate.of(2026, 10, 10));
    private static final String CONSULTA = "?desde=2026-10-04&hasta=2026-10-10";

    private MockRestServiceServer servidor;
    private OrquestadorRestClient cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder();
        servidor = MockRestServiceServer.bindTo(builder).build();
        cliente = new OrquestadorRestClient(builder, BASE, "/api/service/solicitudes/estadisticas",
                "/api/workers/trabajadores/estadisticas", "/api/v1/reputation/estadisticas",
                "/api/users/usuarios/estadisticas");
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void consultaLosTresDominiosPorElOrquestadorReenviandoElJwt() {
        Jwt jwt = Jwt.withTokenValue("token-admin").header("alg", "HS256").subject("admin-1").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        servidor.expect(requestTo(BASE + "/api/service/solicitudes/estadisticas" + CONSULTA))
                .andExpect(header("Authorization", "Bearer token-admin"))
                .andRespond(withSuccess("""
                        {"solicitudesPorEstado": {"CUMPLIDA": 1248},
                         "cumplidosPorDia": [{"etiqueta": "2026-10-04", "cantidad": 142}],
                         "cumplidosPorOficio": []}""", MediaType.APPLICATION_JSON));
        servidor.expect(requestTo(BASE + "/api/workers/trabajadores/estadisticas"))
                .andRespond(withSuccess("{\"activos\": 400, \"pausadosVoluntarios\": 12, \"pausadosPorReputacion\": 6}",
                        MediaType.APPLICATION_JSON));
        servidor.expect(requestTo(BASE + "/api/v1/reputation/estadisticas" + CONSULTA))
                .andRespond(withSuccess("""
                        {"totalResenas": 500, "calificacionPromedio": 4.6, "resenasReportadas": 7,
                         "reportesPendientes": 5, "reportesResueltos": 2}""", MediaType.APPLICATION_JSON));
        servidor.expect(requestTo(BASE + "/api/users/usuarios/estadisticas" + CONSULTA))
                .andExpect(header("Authorization", "Bearer token-admin"))
                .andRespond(withSuccess("""
                        {"cuentasActivas": 1480, "cuentasInactivas": 236,
                         "activosPorDia": [{"etiqueta": "2026-10-04", "cantidad": 600}]}""", MediaType.APPLICATION_JSON));

        EstadisticasServiciosDTO servicios = cliente.estadisticasServicios(SEMANA);
        EstadisticasTrabajadoresDTO trabajadores = cliente.estadisticasTrabajadores();

        assertThat(servicios.solicitudesPorEstado()).containsEntry("CUMPLIDA", 1248L);
        assertThat(servicios.cumplidosPorDia().get(0).cantidad()).isEqualTo(142);
        assertThat(trabajadores.pausadosVoluntarios()).isEqualTo(12);
        assertThat(cliente.estadisticasReputacion(SEMANA).reportesPendientes()).isEqualTo(5);
        assertThat(cliente.estadisticasUsuarios(SEMANA).cuentasInactivas()).isEqualTo(236);
        servidor.verify();
    }

    @Test
    void erroresORespuestasVaciasSonFallasDeIntegracion() {
        servidor.expect(requestTo(BASE + "/api/service/solicitudes/estadisticas" + CONSULTA)).andRespond(withServerError());
        servidor.expect(requestTo(BASE + "/api/workers/trabajadores/estadisticas")).andRespond(withSuccess());
        assertThatThrownBy(() -> cliente.estadisticasServicios(SEMANA)).isInstanceOf(IntegracionException.class);
        assertThatThrownBy(() -> cliente.estadisticasTrabajadores())
                .isInstanceOf(IntegracionException.class)
                .hasMessageContaining("Worker");
    }

    @Test
    void elClienteSimuladoEntregaDatosRepetibles() {
        OrquestadorClientSimulado simulado = new OrquestadorClientSimulado();
        EstadisticasServiciosDTO servicios = simulado.estadisticasServicios(SEMANA);
        assertThat(servicios.cumplidosPorDia()).hasSize(7);
        assertThat(servicios).isEqualTo(simulado.estadisticasServicios(SEMANA));
        assertThat(simulado.estadisticasTrabajadores().pausadosPorReputacion()).isEqualTo(6);
        assertThat(simulado.estadisticasReputacion(SEMANA).resenasReportadas()).isEqualTo(7);
        assertThat(OrquestadorClientSimulado.cumplidosDelDia(LocalDate.of(2026, 10, 4))).isBetween(140L, 145L);
        assertThat(servicios.solicitudesPorOficio()).hasSize(6);
        assertThat(servicios.solicitudesPorZona()).hasSize(6);
        assertThat(servicios.tarifaPromedio()).isPositive();
        assertThat(simulado.estadisticasUsuarios(SEMANA).activosPorDia()).hasSize(7);
        assertThat(OrquestadorClientSimulado.activosDelDia(LocalDate.of(2026, 10, 4))).isBetween(480L, 530L);
    }
}
