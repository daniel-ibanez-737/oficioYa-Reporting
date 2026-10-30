package edu.eci.dosw.oficioya.reporting.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.domain.TipoReporte;
import edu.eci.dosw.oficioya.reporting.dto.ArchivoExportado;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticaResponse;
import edu.eci.dosw.oficioya.reporting.dto.IndicadorResponse;
import edu.eci.dosw.oficioya.reporting.dto.ReporteResponse;
import edu.eci.dosw.oficioya.reporting.dto.TipoReporteResponse;
import edu.eci.dosw.oficioya.reporting.service.ReporteService;

/**
 * API de Reporting. Todo es exclusivo del administrador (ver SecurityConfig).
 * Criterio de consulta (RF-04): periodo con {@code desde} y {@code hasta} (yyyy-MM-dd);
 * sin fechas se usan los últimos 7 días.
 */
@Tag(name = "Reportes", description = "Reportes, indicadores, estadísticas y exportación (administrador)")
@RestController
@RequestMapping("/api/reporting")
public class ReporteController {

    private final ReporteService servicio;

    public ReporteController(ReporteService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Catálogo de reportes disponibles")
    @GetMapping("/reportes")
    public List<TipoReporteResponse> catalogo() {
        return servicio.catalogo().stream().map(TipoReporteResponse::desde).toList();
    }

    @Operation(summary = "Generar un reporte por tipo y periodo")
    @GetMapping("/reportes/{tipo}")
    public ReporteResponse generar(@PathVariable TipoReporte tipo,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                   @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return ReporteResponse.desde(servicio.generar(tipo, servicio.periodo(desde, hasta), jwt.getSubject()));
    }

    @Operation(summary = "Exportar un reporte como archivo (CSV)")
    @GetMapping("/reportes/{tipo}/exportacion")
    public ResponseEntity<byte[]> exportar(@PathVariable TipoReporte tipo,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                           @RequestParam(defaultValue = "CSV") FormatoExportacion formato,
                                           @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        PeriodoConsulta periodo = servicio.periodo(desde, hasta);
        ArchivoExportado archivo = servicio.exportar(tipo, periodo, formato, jwt.getSubject());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.tipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo.nombre()).build().toString())
                .body(archivo.contenido());
    }

    @Operation(summary = "Indicadores comparados con el periodo anterior")
    @GetMapping("/indicadores")
    public List<IndicadorResponse> indicadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return servicio.indicadores(servicio.periodo(desde, hasta), jwt.getSubject()).stream()
                .map(IndicadorResponse::desde).toList();
    }

    @Operation(summary = "Estadísticas agregadas del periodo")
    @GetMapping("/estadisticas")
    public List<EstadisticaResponse> estadisticas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return servicio.estadisticas(servicio.periodo(desde, hasta), jwt.getSubject()).stream()
                .map(EstadisticaResponse::desde).toList();
    }
}
