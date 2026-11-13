package edu.eci.dosw.oficioya.reporting.service.exportacion;

import edu.eci.dosw.oficioya.reporting.domain.FormatoExportacion;
import edu.eci.dosw.oficioya.reporting.domain.Reporte;

/**
 * Estrategia de exportación de un reporte a un formato de archivo (RF-08).
 */
public interface ExportadorReporte {

    FormatoExportacion formato();

    String tipoContenido();

    String extension();

    byte[] exportar(Reporte reporte);
}
