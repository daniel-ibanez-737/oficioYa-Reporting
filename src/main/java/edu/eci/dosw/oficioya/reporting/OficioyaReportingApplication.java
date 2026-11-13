package edu.eci.dosw.oficioya.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio oficioya-reporting.
 * Reportes, indicadores y estadísticas para el administrador, con exportación. Consulta otros dominios a través del Orchestrator.
 */
@SpringBootApplication
public class OficioyaReportingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OficioyaReportingApplication.class, args);
    }
}
