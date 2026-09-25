# 📄Requerimientos del Sistema

## 1. Lista general de requerimientos

El sistema de Reporting (OficioYa) debe tener los siguientes requerimientos:

### 1.1 Requerimientos funcionales

1. Permitir al administrador visualizar reportes de información de la plataforma.
2. Permitir al administrador consultar indicadores de desempeño de la plataforma.
3. Permitir al administrador consultar estadísticas de la plataforma.
4. Permitir al administrador exportar los reportes generados por la plataforma.

### 1.2 Requerimientos no funcionales

1. Cobertura de pruebas unitarias mínima del 80%.
2. La interfaz de reportes, indicadores y estadísticas debe ser responsive.
3. El sistema debe registrar logs de cada generación y exportación de reporte.

## 2. Diagramas de caso de uso

### 2.1 Requerimiento Funcional 1

| Campo | Descripción |
|------|-------------|
| **ID** | RF-01 |
| **Nombre del requerimiento** | Visualización de reportes de la plataforma |
| **Descripción** | *El sistema debe permitir al administrador visualizar reportes con información disponible de la plataforma* |
| **Precondiciones** | *El administrador debe estar autenticado y autorizado para acceder al módulo de Reporting* |
| **Actor** | *Administrador* |
| **Flujo principal** | 1. El administrador accede al módulo de Reporting.<br>2. Selecciona la opción de reportes.<br>3. El sistema obtiene la información requerida desde los dominios correspondientes.<br>4. El sistema genera y presenta el reporte solicitado. |
| **Poscondiciones** | *El administrador visualiza el reporte generado con la información disponible de la plataforma.* |

### 2.2 Requerimiento Funcional 2

| Campo | Descripción |
|------|-------------|
| **ID** | RF-02 |
| **Nombre del requerimiento** | Consulta de indicadores de desempeño |
| **Descripción** | *El sistema debe permitir al administrador consultar indicadores de desempeño de la plataforma* |
| **Precondiciones** | *El administrador debe estar autenticado y debe existir información disponible para calcular los indicadores* |
| **Actor** | *Administrador* |
| **Flujo principal** | 1. El administrador accede al módulo de Reporting.<br>2. Selecciona la opción de indicadores.<br>3. El sistema obtiene la información necesaria desde los dominios correspondientes.<br>4. El sistema calcula y presenta los indicadores disponibles. |
| **Poscondiciones** | *El administrador visualiza los indicadores de desempeño disponibles de la plataforma.* |

### 2.3 Requerimiento Funcional 3

| Campo | Descripción |
|------|-------------|
| **ID** | RF-03 |
| **Nombre del requerimiento** | Consulta de estadísticas de la plataforma |
| **Descripción** | *El sistema debe permitir al administrador consultar estadísticas generadas a partir de la información disponible de la plataforma* |
| **Precondiciones** | *El administrador debe estar autenticado y debe existir información disponible para generar las estadísticas* |
| **Actor** | *Administrador* |
| **Flujo principal** | 1. El administrador accede al módulo de Reporting.<br>2. Selecciona la opción de estadísticas.<br>3. El sistema obtiene la información requerida desde los dominios correspondientes.<br>4. El sistema procesa la información y presenta las estadísticas disponibles. |
| **Poscondiciones** | *El administrador visualiza las estadísticas generadas por el sistema.* |

### 2.4 Requerimiento Funcional 4

| Campo | Descripción |
|------|-------------|
| **ID** | RF-04 |
| **Nombre del requerimiento** | Exportación de reportes |
| **Descripción** | *El sistema debe permitir al administrador exportar los reportes generados por la plataforma* |
| **Precondiciones** | *El administrador debe estar autenticado y debe existir un reporte generado disponible para exportación* |
| **Actor** | *Administrador* |
| **Flujo principal** | 1. El administrador genera o visualiza un reporte.<br>2. Selecciona la opción de exportar.<br>3. El sistema prepara el reporte para su exportación.<br>4. El sistema entrega el archivo generado al administrador. |
| **Poscondiciones** | *El reporte queda disponible para que el administrador lo conserve o utilice fuera de la plataforma.* |
