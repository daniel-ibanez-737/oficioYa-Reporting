package edu.eci.dosw.oficioya.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.jayway.jsonpath.JsonPath;

/**
 * Pruebas de aceptación de Reporting de punta a punta con el perfil {@code demo} (Orchestrator simulado).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class AceptacionReportingDemoTest {

    private static final String BASE = "/api/reporting";

    @Autowired
    private MockMvc mvc;

    private static RequestPostProcessor administrador() {
        return jwt().jwt(j -> j.subject("admin-1")).authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }

    @Test
    void rf1CatalogoDeReportes() throws Exception {
        mvc.perform(get(BASE + "/reportes").with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].tipo").value("SERVICIOS"));
    }

    @Test
    void rf1Rf4ReportePorTipoYPeriodo() throws Exception {
        mvc.perform(get(BASE + "/reportes/GENERAL").param("desde", "2026-10-04").param("hasta", "2026-10-10")
                        .with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Resumen de la plataforma"))
                .andExpect(jsonPath("$.desdeComparacion").value("2026-09-27"))
                .andExpect(jsonPath("$.indicadores.length()").value(6))
                .andExpect(jsonPath("$.indicadores[1].detalle").value("12 voluntarios · 6 por revisión"))
                .andExpect(jsonPath("$.estadisticas[0].puntos.length()").value(7));
    }

    @Test
    void rf2Rf3IndicadoresYEstadisticas() throws Exception {
        mvc.perform(get(BASE + "/indicadores").with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("SERVICIOS_CUMPLIDOS"))
                .andExpect(jsonPath("$[0].valorAnterior").isNumber());
        mvc.perform(get(BASE + "/estadisticas").param("desde", "2026-10-01").param("hasta", "2026-10-31")
                        .with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].puntos.length()").value(31));
    }

    @Test
    void rf8LaExportacionCoincideConLoConsultado() throws Exception {
        String json = mvc.perform(get(BASE + "/reportes/SERVICIOS").param("desde", "2026-10-04")
                        .param("hasta", "2026-10-10").with(administrador()))
                .andReturn().getResponse().getContentAsString();
        Number cumplidos = JsonPath.read(json, "$.indicadores[0].valor");

        byte[] archivo = mvc.perform(get(BASE + "/reportes/SERVICIOS/exportacion").param("desde", "2026-10-04")
                        .param("hasta", "2026-10-10").param("formato", "CSV").with(administrador()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"reporte-servicios-2026-10-04_2026-10-10.csv\""))
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(new String(archivo, StandardCharsets.UTF_8))
                .contains("Total de servicios cumplidos;" + cumplidos.longValue() + ";");
    }

    @Test
    void criteriosDeConsultaInvalidosResponden400() throws Exception {
        mvc.perform(get(BASE + "/reportes/INEXISTENTE").with(administrador())).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/reportes/GENERAL").param("desde", "2026-10-10").param("hasta", "2026-10-01")
                        .with(administrador()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isNotEmpty());
        mvc.perform(get(BASE + "/indicadores").param("desde", "2026-10-10").with(administrador()))
                .andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/reportes/GENERAL/exportacion").param("formato", "XLS").with(administrador()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void soloElAdministradorPuedeConsultarReportes() throws Exception {
        mvc.perform(get(BASE + "/reportes")).andExpect(status().isUnauthorized());
        mvc.perform(get(BASE + "/reportes").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CONTRATANTE"))))
                .andExpect(status().isForbidden());
        mvc.perform(get(BASE + "/reportes/GENERAL/exportacion")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_TRABAJADOR"))))
                .andExpect(status().isForbidden());
    }
}
