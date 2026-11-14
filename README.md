# oficioya-reporting

Microservicio del dominio **Reporting** de OficioYa (Squad 5).

Reportes, indicadores y estadísticas para el administrador, con exportación. Consulta otros dominios a través del Orchestrator.

## Tecnología
- Java 21, Spring Boot 4.1.1, Maven
- Spring Security con JWT (validado también por el Orchestrator)
- JUnit 5 y JaCoCo (cobertura mínima del 80 %)

## Estructura por capas (`edu.eci.dosw.oficioya.reporting`)
| Paquete | Capa |
|---|---|
| `controller` | Presentación (API REST) |
| `service` | Aplicación (casos de uso) |
| `domain` | Dominio (modelo y reglas de negocio) |
| `dto` | Objetos de transferencia |
| `client` | Infraestructura: `OrquestadorClient`, única salida hacia otros dominios o la IA |
| `config` | Configuración (seguridad, clientes HTTP) |
| `exception` | Excepciones y manejo de errores |

Toda comunicación con otros dominios o con la IA **entra y sale por el Orchestrator**.

## Compilar y probar
Con Java 21 instalado (no hace falta Maven: el proyecto trae Maven Wrapper):

```bash
./mvnw verify          # Linux, macOS o Git Bash
mvnw.cmd verify        # Windows (CMD o PowerShell)
```

Sin Java, con Docker:

```bash
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn verify
```

`mvn verify` compila, ejecuta las pruebas y valida la cobertura mínima con JaCoCo
(reporte en `target/site/jacoco/index.html`).

## Variables de entorno
| Variable | Uso | Valor por defecto (desarrollo) |
|---|---|---|
| `JWT_SECRET` | Secreto compartido con Identity, en Base64 (igual que Identity) | valor de desarrollo |
| `JWT_ROLES_CLAIM` | Claim del JWT con los roles | `roles` |
| `ORQUESTADOR_URL` | URL del Orchestrator | `http://localhost:8080` |

## Endpoints (solo rol `ADMIN`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/reporting/reportes` | Catálogo de reportes disponibles. |
| GET | `/api/v1/reporting/reportes/{tipo}?desde&hasta` | Genera el reporte: `SERVICIOS`, `RESENAS`, `TRABAJADORES`, `USUARIOS` o `GENERAL`. |
| GET | `/api/v1/reporting/reportes/{tipo}/exportacion?desde&hasta&formato=CSV` | Descarga el mismo reporte como archivo. |
| GET | `/api/v1/reporting/indicadores?desde&hasta` | Los 11 indicadores, comparados con el periodo anterior cuando aplica. |
| GET | `/api/v1/reporting/estadisticas?desde&hasta` | Las 10 estadísticas agregadas. |

- Fechas en formato `yyyy-MM-dd`. Sin fechas se usan los últimos 7 días. El periodo máximo es de 366 días.
- Errores: `400` criterio inválido, `401` sin JWT, `403` sin rol de administrador, `502` si otro dominio no responde.

**Indicadores:** servicios cumplidos, trabajadores pausados (voluntarios y por revisión), reseñas reportadas
(pendientes y resueltas), tasa de cancelación, trabajadores activos, calificación promedio, cuentas activas,
cuentas inactivas, promedio de usuarios activos por día, categoría más apetecida y tarifa promedio.

**Estadísticas nuevas (review del Sprint 4):** flujo de trabajo (solicitudes por etapa), cuentas activas e inactivas,
usuarios activos por día, categorías más apetecidas, zonas más concurridas y tarifa promedio por oficio.

**Estadísticas:** servicios cumplidos por día y por oficio, solicitudes por estado, trabajadores activos por oficio
y reportes de reseñas por estado.

## De dónde salen los datos
Reporting no guarda nada: en cada consulta pide datos **agregados** (conteos y promedios, nunca datos personales)
a través del Orchestrator.

| Dominio | Ruta (configurable en `oficioya.orquestador.rutas`) |
|---|---|
| Service | `GET /api/v1/services/reports?from=&to=` (total, por estado, por barrio y por hora; las horas se agrupan en franjas de 6 horas) |
| Worker | `/api/workers/trabajadores/estadisticas` |
| Reputation | `/api/v1/reputation/estadisticas` (ya implementado por el Squad 5) |

## Formato del JWT (acordado con Identity, Squad 1)
- Firma HS256 con el secreto decodificado de Base64.
- `sub`: id (UUID) del usuario.
- `roles`: lista sin prefijo (`CONTRATANTE`, `TRABAJADOR`, `ADMIN`, `SISTEMA`).

## Perfil `demo`
Para probar sin los otros dominios, con cifras parecidas a las del mockup:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

## Supuestos pendientes de confirmar
- Service (Squad 4) ya entrega el reporte real; falta que agregue cumplidos por día y por oficio, solicitudes por
  oficio y tarifas (RF-03 y RF-05). Los estados se traducen al español (Pendiente, Aceptada, Conflicto, Cumplida,
  Rechazada, Cancelada, Expirada, Eliminada).
- Formato de las estadísticas de Worker (Squad 3).
- Identity / Usuarios debe exponer cuentas activas, inactivas y usuarios activos por día
  (`oficioya.orquestador.rutas.estadisticas-usuarios`, por defecto `/api/users/usuarios/estadisticas`).
- Exportación en CSV; PDF se agrega creando `ExportadorPdf`.
- La "actividad reciente" del mockup requiere un registro de eventos que hoy no existe en ningún dominio.


## Documentación de la API (Swagger)
Con el servicio en ejecución:
- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI (JSON): `http://localhost:8083/v3/api-docs`

Para probar desde Swagger UI, se pulsa **Authorize** y se pega un JWT.
La colección de Postman del servicio (`postman/`) genera tokens de prueba para cada rol y se ejecuta sola con `bash postman/ejecutar-coleccion.sh`.
La documentación es pública y se puede apagar con `SWAGGER_ENABLED=false`.
