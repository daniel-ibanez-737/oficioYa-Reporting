package edu.eci.dosw.oficioya.reporting.client;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.eci.dosw.oficioya.reporting.domain.PeriodoConsulta;
import edu.eci.dosw.oficioya.reporting.dto.ConteoDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasReputacionDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasServiciosDTO;
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasTrabajadoresDTO;

/**
 * Orchestrator simulado para el perfil {@code demo}, con cifras parecidas a las del mockup de administración.
 * Los valores dependen solo de las fechas, así que son repetibles.
 */
@Component
@Profile("demo")
public class OrquestadorClientSimulado implements OrquestadorClient {

    private static final String[] OFICIOS = {"Plomería", "Electricidad", "Carpintería", "Profesor particular",
        "Cuidado de mascotas", "Técnico de electrodomésticos"};
    private static final int[] PESO_OFICIO = {30, 25, 15, 12, 10, 8};

    /** Servicios cumplidos de un día: más entre semana, con una pequeña variación según la fecha. */
    static long cumplidosDelDia(LocalDate fecha) {
        long base = fecha.getDayOfWeek() == DayOfWeek.SUNDAY ? 140 : 160 + fecha.getDayOfWeek().getValue() * 8L;
        return base + fecha.toEpochDay() % 5;
    }

    @Override
    public EstadisticasServiciosDTO estadisticasServicios(PeriodoConsulta periodo) {
        List<ConteoDTO> porDia = periodo.fechas().stream()
                .map(f -> new ConteoDTO(f.toString(), cumplidosDelDia(f)))
                .toList();
        long cumplidos = porDia.stream().mapToLong(ConteoDTO::cantidad).sum();
        List<ConteoDTO> porOficio = new ArrayList<>();
        for (int i = 0; i < OFICIOS.length; i++) {
            porOficio.add(new ConteoDTO(OFICIOS[i], cumplidos * PESO_OFICIO[i] / 100));
        }
        long cancelados = cumplidos / 20;
        Map<String, Long> porEstado = Map.of("CUMPLIDA", cumplidos, "CANCELADA", cancelados,
                "EN_CURSO", cumplidos / 10, "PENDIENTE", cumplidos / 15);
        return new EstadisticasServiciosDTO(porEstado, porDia, porOficio);
    }

    @Override
    public EstadisticasTrabajadoresDTO estadisticasTrabajadores() {
        List<ConteoDTO> porOficio = new ArrayList<>();
        for (int i = 0; i < OFICIOS.length; i++) {
            porOficio.add(new ConteoDTO(OFICIOS[i], PESO_OFICIO[i] * 4L));
        }
        return new EstadisticasTrabajadoresDTO(400, 12, 6, porOficio);
    }

    @Override
    public EstadisticasReputacionDTO estadisticasReputacion(PeriodoConsulta periodo) {
        long resenas = periodo.dias() * 95;
        return new EstadisticasReputacionDTO(resenas, 4.6, 7, 5, 2);
    }
}
