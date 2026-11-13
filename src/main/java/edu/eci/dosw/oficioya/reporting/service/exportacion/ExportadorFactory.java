package edu.eci.dosw.oficioya.reporting.service.exportacion;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.exception.SolicitudInvalidaException;

/**
 * Entrega el exportador de cada formato. Spring inyecta todos los {@link ExportadorReporte} disponibles,
 * así que agregar PDF solo requiere crear la clase {@code ExportadorPdf}.
 */
@Component
public class ExportadorFactory {

    private final Map<FormatoExportacion, ExportadorReporte> exportadores = new EnumMap<>(FormatoExportacion.class);

    public ExportadorFactory(List<ExportadorReporte> disponibles) {
        disponibles.forEach(e -> exportadores.put(e.formato(), e));
    }

    public ExportadorReporte obtener(FormatoExportacion formato) {
        ExportadorReporte exportador = exportadores.get(formato);
        if (exportador == null) {
            throw new SolicitudInvalidaException("El formato " + formato + " no está disponible");
        }
        return exportador;
    }
}
