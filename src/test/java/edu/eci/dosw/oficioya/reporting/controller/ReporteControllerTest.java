package edu.eci.dosw.oficioya.reporting.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.eci.dosw.oficioya.reporting.config.SecurityConfig;
import edu.eci.dosw.oficioya.reporting.exception.IntegracionException;
import edu.eci.dosw.oficioya.reporting.service.ReporteService;

@WebMvcTest(ReporteController.class)
@Import(SecurityConfig.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReporteService servicio;

    @Test
    void siUnDominioNoRespondeSeDevuelve502() throws Exception {
        when(servicio.periodo(any(), any())).thenThrow(new IntegracionException("caído", new RuntimeException()));
        mvc.perform(get("/api/v1/reporting/indicadores")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadGateway());
    }
}
