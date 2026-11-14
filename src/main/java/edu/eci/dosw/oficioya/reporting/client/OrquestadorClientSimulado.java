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
import edu.eci.dosw.oficioya.reporting.dto.EstadisticasUsuariosDTO;

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
    /** Peso de cada oficio en las solicitudes (la demanda no es igual a lo cumplido). */
    private static final int[] PESO_DEMANDA = {28, 22, 12, 18, 9, 11};
    private static final long[] TARIFA_OFICIO = {85_000, 70_000, 95_000, 45_000, 40_000, 75_000};
    private static final String[] ZONAS = {"Chapinero", "Usaquén", "Suba", "Kennedy", "Engativá", "Teusaquillo"};
    private static final int[] PESO_ZONA = {24, 21, 18, 15, 12, 10};
    // Franjas de 6 horas, en el mismo texto que usa ReporteServiceDTO con los datos reales de Service.
    private static final String[] FRANJAS = {"Madrugada (00:00 – 06:00)", "Mañana (06:00 – 12:00)",
            "Tarde (12:00 – 18:00)", "Noche (18:00 – 24:00)"};
    private static final int[] PESO_FRANJA = {4, 41, 36, 19};

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
        // Los 8 estados de Service (Squad 4), ya traducidos al español.
        Map<String, Long> porEstado = Map.of("CUMPLIDA", cumplidos, "CANCELADA", cancelados,
                "ACEPTADA", cumplidos / 10, "PENDIENTE", cumplidos / 15, "RECHAZADA", cumplidos / 25,
                "EXPIRADA", cumplidos / 40, "CONFLICTO", cumplidos / 60, "ELIMINADA", cumplidos / 80);
        long solicitudes = porEstado.values().stream().mapToLong(Long::longValue).sum();
        List<ConteoDTO> solicitudesPorOficio = repartir(OFICIOS, PESO_DEMANDA, solicitudes);
        List<ConteoDTO> solicitudesPorZona = repartir(ZONAS, PESO_ZONA, solicitudes);
        // la tarifa varía un poco según el periodo para que la comparación tenga sentido
        long ajuste = periodo.desde().toEpochDay() / 7 % 5 * 750;
        List<ConteoDTO> tarifaPorOficio = new ArrayList<>();
        double tarifaPonderada = 0;
        for (int i = 0; i < OFICIOS.length; i++) {
            long tarifa = TARIFA_OFICIO[i] + ajuste;
            tarifaPorOficio.add(new ConteoDTO(OFICIOS[i], tarifa));
            tarifaPonderada += tarifa * PESO_DEMANDA[i] / 100.0;
        }
        long sinEliminadas = solicitudes - porEstado.get("ELIMINADA");
        List<ConteoDTO> solicitudesPorFranja = repartir(FRANJAS, PESO_FRANJA, sinEliminadas);
        return new EstadisticasServiciosDTO(porEstado, porDia, porOficio, solicitudesPorOficio, solicitudesPorZona,
                (double) Math.round(tarifaPonderada), tarifaPorOficio, solicitudesPorFranja);
    }

    private static List<ConteoDTO> repartir(String[] etiquetas, int[] pesos, long total) {
        List<ConteoDTO> conteos = new ArrayList<>();
        for (int i = 0; i < etiquetas.length; i++) {
            conteos.add(new ConteoDTO(etiquetas[i], total * pesos[i] / 100));
        }
        return conteos;
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

    /** Usuarios activos de un día: más entre semana, con una pequeña variación según la fecha. */
    static long activosDelDia(LocalDate fecha) {
        long base = fecha.getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue() ? 480 : 620;
        return base + fecha.toEpochDay() % 9 * 6;
    }

    @Override
    public EstadisticasUsuariosDTO estadisticasUsuarios(PeriodoConsulta periodo) {
        List<ConteoDTO> porDia = periodo.fechas().stream()
                .map(f -> new ConteoDTO(f.toString(), activosDelDia(f)))
                .toList();
        return new EstadisticasUsuariosDTO(1_480, 236, porDia);
    }
}
