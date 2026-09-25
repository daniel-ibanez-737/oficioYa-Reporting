# 📄 Requerimientos del Sistema

## 1. Lista general de requerimientos

El sistema de Reporting (OficioYa) debe tener los siguientes requerimientos:

### 1.1 Requerimientos funcionales

1. Permitir al trabajador consultar su historial de trabajos completados y ganancias referenciales del mes.
2. Permitir al contratante consultar su historial de contrataciones y recontactar a un trabajador con un clic.
3. Generar un reporte administrativo de las búsquedas más frecuentes por oficio y zona.
4. Permitir al trabajador con suscripción Pro consultar estadísticas avanzadas de demanda de sus servicios.

### 1.2 Requerimientos no funcionales

1. Todos los endpoints deben validar el token JWT.
2. Cobertura de pruebas unitarias mínima del 80%.
3. La interfaz de historial y estadísticas debe ser responsive.
4. El sistema debe registrar logs de cada generación de reporte.
5. La generación de reportes históricos no debe tardar más de 5 segundos para periodos de hasta 1 año. (borrador, validar con PO)

## 2. Diagramas de caso de uso

### 2.1 Requerimiento Funcional 1

| Campo | Descripción |
|------|-------------|
| **ID** | RF-01 |
| **Nombre del requerimiento** | Historial de trabajos completados |
| **Descripción** | *El sistema debe permitir al trabajador consultar cuántos trabajos ha completado y sus ganancias referenciales del mes* |
| **Precondiciones** | *El trabajador debe estar autenticado* |
| **Actor** | *Trabajador* |
| **Flujo principal** | 1. El trabajador accede a su historial.<br>2. El sistema agrega los trabajos cumplidos.<br>3. El sistema calcula las ganancias referenciales del periodo. |
| **Poscondiciones** | *El trabajador visualiza su historial y ganancias referenciales.* |

### 2.2 Requerimiento Funcional 2

| Campo | Descripción |
|------|-------------|
| **ID** | RF-02 |
| **Nombre del requerimiento** | Historial de contrataciones |
| **Descripción** | *El sistema debe permitir al contratante ver su historial de servicios contratados y recontactar a un trabajador con un clic* |
| **Precondiciones** | *El contratante debe estar autenticado* |
| **Actor** | *Contratante* |
| **Flujo principal** | 1. El contratante accede a su historial.<br>2. El sistema lista los servicios previos.<br>3. El contratante selecciona "recontactar". |
| **Poscondiciones** | *El contratante puede iniciar una nueva solicitud al mismo trabajador.* |

### 2.3 Requerimiento Funcional 3

| Campo | Descripción |
|------|-------------|
| **ID** | RF-03 |
| **Nombre del requerimiento** | Reporte de búsquedas más frecuentes |
| **Descripción** | *El sistema debe generar un reporte administrativo de las búsquedas más frecuentes por oficio y zona* |
| **Precondiciones** | *El administrador debe estar autenticado. Debe existir historial de búsquedas* |
| **Actor** | *Administrador* |
| **Flujo principal** | 1. El administrador solicita el reporte.<br>2. El sistema agrega las búsquedas históricas.<br>3. El sistema retorna el reporte consolidado. |
| **Poscondiciones** | *El administrador visualiza el reporte de tendencias de búsqueda.* |

### 2.4 Requerimiento Funcional 4

| Campo | Descripción |
|------|-------------|
| **ID** | RF-04 |
| **Nombre del requerimiento** | Estadísticas avanzadas de demanda |
| **Descripción** | *El sistema debe permitir a un trabajador con suscripción Pro consultar estadísticas avanzadas de demanda de sus servicios* |
| **Precondiciones** | *El trabajador debe tener suscripción Pro activa* |
| **Actor** | *Trabajador (Pro)* |
| **Flujo principal** | 1. El trabajador Pro accede a estadísticas avanzadas.<br>2. El sistema calcula métricas de demanda (búsquedas que lo incluyeron, tendencia). |
| **Poscondiciones** | *El trabajador visualiza sus estadísticas avanzadas de demanda.* |