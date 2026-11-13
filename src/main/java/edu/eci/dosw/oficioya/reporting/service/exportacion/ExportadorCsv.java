package edu.eci.dosw.oficioya.reporting.service.exportacion;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.Estadistica;
import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.domain.Indicador;
import edu.eci.dosw.oficioya.reporting.domain.PuntoEstadistico;
import edu.eci.dosw.oficioya.reporting.domain.Reporte;

/**
 * Exporta un reporte a CSV con separador ';' y coma decimal, para que Excel en español lo abra bien.
 * Incluye exactamente los mismos indicadores y estadísticas que se ven en la consulta (RNF-06).
 */
@Component
public class ExportadorCsv implements ExportadorReporte {

    static final char SEPARADOR = ';';
    private static final String BOM = "﻿";
    private static final String SALTO = "\r\n";

    @Override
    public FormatoExportacion formato() {
        return FormatoExportacion.CSV;
    }

    @Override
    public String tipoContenido() {
        return "text/csv;charset=UTF-8";
    }

    @Override
    public String extension() {
        return "csv";
    }

    @Override
    public byte[] exportar(Reporte reporte) {
        StringBuilder csv = new StringBuilder(BOM);
        fila(csv, "Reporte", reporte.titulo());
        fila(csv, "Periodo", reporte.periodo().desde().toString(), reporte.periodo().hasta().toString());
        fila(csv, "Comparado con", reporte.periodo().periodoAnterior().desde().toString(),
                reporte.periodo().periodoAnterior().hasta().toString());
        fila(csv, "Generado", reporte.generadoEn().toString());

        csv.append(SALTO);
        fila(csv, "Indicadores");
        fila(csv, "Indicador", "Valor", "Periodo anterior", "Variación %", "Unidad", "Detalle");
        for (Indicador i : reporte.indicadores()) {
            fila(csv, i.nombre(), numero(i.valor()), numero(i.valorAnterior()), numero(i.variacionPorcentual()),
                    i.unidad(), i.detalle());
        }

        for (Estadistica e : reporte.estadisticas()) {
            csv.append(SALTO);
            fila(csv, e.nombre());
            fila(csv, "Etiqueta", "Valor");
            for (PuntoEstadistico p : e.puntos()) {
                fila(csv, p.etiqueta(), numero(p.valor()));
            }
            fila(csv, "Total", numero(e.total()));
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void fila(StringBuilder csv, String... campos) {
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                csv.append(SEPARADOR);
            }
            csv.append(escapar(campos[i]));
        }
        csv.append(SALTO);
    }

    /**
     * Escapa un campo de texto: comillas si contiene separador, comillas o saltos de línea, y un apóstrofo
     * delante si empieza por = + - @ para evitar que Excel lo ejecute como fórmula (inyección CSV).
     */
    static String escapar(String campo) {
        if (campo == null) {
            return "";
        }
        String valor = campo;
        if (!valor.isEmpty() && "=+-@".indexOf(valor.charAt(0)) >= 0 && !esNumero(valor)) {
            valor = "'" + valor;
        }
        if (valor.indexOf(SEPARADOR) >= 0 || valor.contains("\"") || valor.contains("\n") || valor.contains("\r")) {
            valor = "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private static boolean esNumero(String valor) {
        return valor.matches("-?\\d+(,\\d+)?");
    }

    /** Número con coma decimal y hasta dos decimales; vacío si no hay valor. */
    static String numero(Double valor) {
        if (valor == null) {
            return "";
        }
        DecimalFormat formato = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("es-CO")));
        return formato.format(valor);
    }
}
