# Colección de Postman: Reporting

`OficioYa-Reporting.postman_collection.json` reúne los **casos de aceptación** del sprint 3 de Reporting, más una verificación de su documentación Swagger.

| Carpeta | Peticiones | Qué demuestra |
|---|---|---|
| Reporting | 8 | Catálogo, reporte por periodo comparado con el anterior, indicadores, estadísticas, **CSV con los mismos datos que la consulta**, periodo inválido (400), 403 y 401. |
| Documentación (Swagger) | 1 | `/v3/api-docs` publicado, con el esquema JWT. |

Cada petición tiene pruebas (`pm.test`) que verifican el código HTTP y el contenido.

## Tokens
No hay que pegar tokens a mano. El *pre-request* de la colección genera un JWT HS256 para cada rol:
- `token_contratante`
- `token_trabajador`
- `token_admin`
- `token_sistema`

Para firmarlos usa la variable `jwtSecret`, que es el mismo secreto de desarrollo de `application.yml`.

## Opción 1: Postman (app)
1. Importar `OficioYa-Reporting.postman_collection.json`.
2. Levantar el servicio: `./mvnw spring-boot:run -Dspring-boot.run.profiles=demo` (perfil `demo`: cifras simuladas, sin depender de otros servicios).
3. Hacer clic derecho en la colección y elegir **Run collection**.

Para apuntar a otro entorno, cambia las variables `reportingUrl` y `jwtSecret` de la colección.

## Opción 2: todo automático con Docker y Newman
Primero se compila el proyecto con `./mvnw verify`. Luego, desde la raíz del proyecto:

```bash
bash postman/ejecutar-coleccion.sh
```

El script hace lo siguiente:
1. Levanta el servicio (perfil `demo`) en contenedores.
2. Ejecuta la colección con Newman.
3. Muestra los logs relevantes (reportes generados y exportados).
4. Borra los contenedores.

Resultado esperado: **9 peticiones y 16 aserciones, 0 fallidas**.
