# Product Backlog — Sistema de Gestión de Citas

**Proyecto:** Clínica — DWF901, Universidad Don Bosco
**Repositorio:** https://github.com/Fercho0409/clinica-citas-dwf901
**Última actualización:** 17 de septiembre de 2026

---

## Perfiles de usuario

| Perfil | Descripción |
|--------|-------------|
| **Recepcionista** | Registra pacientes, agenda citas y administra la agenda diaria |
| **Médico** | Consulta su agenda y los datos de los pacientes que atiende |
| **Administrador** | Gestiona médicos, especialidades y usuarios del sistema |
| **Paciente** | Consulta sus propias citas desde un portal externo (Fase 3) |

---

## Fase 1 — Sprint I: Fundamentos Java Web y Arquitectura MVC

### Módulo de Pacientes

| ID | Historia de usuario | Prioridad | Estado |
|----|---------------------|-----------|--------|
| HU-01 | Como **recepcionista**, quiero ver el listado de todos los pacientes registrados para localizar rápidamente a una persona | Alta | Completada |
| HU-02 | Como **recepcionista**, quiero registrar un paciente nuevo con sus datos personales para incorporarlo al sistema | Alta | Completada |
| HU-03 | Como **recepcionista**, quiero editar los datos de un paciente para corregir errores o actualizar su información de contacto | Alta | Completada |
| HU-04 | Como **recepcionista**, quiero eliminar un paciente registrado por error para mantener limpia la base de datos | Media | Completada |
| HU-05 | Como **recepcionista**, quiero que el sistema me impida registrar dos pacientes con el mismo DUI para evitar expedientes duplicados | Alta | Completada |

**Criterios de aceptación de HU-02**
- El formulario solicita nombres, apellidos, DUI, fecha de nacimiento, teléfono, correo y dirección
- Nombres, apellidos y DUI son obligatorios, validados en cliente y servidor
- Al guardar correctamente el sistema redirige al listado y el paciente aparece en la tabla
- Si el DUI ya existe, se muestra un mensaje indicando cuál es el duplicado

### Módulo de Médicos

| ID | Historia de usuario | Prioridad | Estado |
|----|---------------------|-----------|--------|
| HU-06 | Como **administrador**, quiero ver el listado de médicos con su especialidad para conocer el personal disponible | Alta | Completada |
| HU-07 | Como **administrador**, quiero registrar un médico nuevo asignándole una especialidad para incorporarlo al cuadro médico | Alta | Completada |
| HU-08 | Como **administrador**, quiero editar los datos de un médico para mantener su información actualizada | Media | Completada |
| HU-09 | Como **administrador**, quiero eliminar un médico que ya no labora en la clínica para reflejar el personal actual | Media | Completada |
| HU-10 | Como **administrador**, quiero seleccionar la especialidad desde una lista desplegable para evitar errores de escritura | Media | Completada |

**Criterios de aceptación de HU-10**
- El formulario muestra las especialidades por nombre, no por número identificador
- La lista se carga desde la base de datos, no está escrita en la vista
- Al editar un médico, su especialidad actual aparece preseleccionada
- La especialidad es obligatoria

### Tareas técnicas

| ID | Tarea | Responsable | Estado |
|----|-------|-------------|--------|
| HT-01 | Configurar el proyecto Maven con arquitectura MVC y las cuatro capas separadas | Integrante 5 | Completada |
| HT-02 | Implementar la conexión a MySQL con credenciales en archivo externo | Integrante 5 | Completada |
| HT-03 | Construir los POJOs del modelo alineados con el esquema de la base | Integrante 5 | Completada |
| HT-04 | Diseñar la base de datos y generar el script de creación con datos de prueba | Integrante 4 | Completada |
| HT-05 | Implementar las clases DAO con consultas parametrizadas | Integrante 3 | Completada |
| HT-06 | Implementar los Servlets controladores | Integrante 2 | Completada |
| HT-07 | Construir las vistas JSP con JSTL | Integrante 1 | Completada |
| HT-08 | Integrar las capas y verificar el flujo completo | Integrante 5 | Completada |
| HT-09 | Documentar instalación, ejecución e incidencias en el README | Integrante 5 | Completada |
| HT-10 | Elaborar el PDF de evidencias | Integrante 5 | En progreso |

---

## Fases posteriores

Historias identificadas, fuera del alcance del Sprint I. Se detallarán al inicio de cada fase.

### Fase 2 — Persistencia empresarial e integración JSF

| ID | Historia de usuario | Prioridad |
|----|---------------------|-----------|
| HU-11 | Como **recepcionista**, quiero agendar una cita seleccionando paciente, médico, fecha y hora | Alta |
| HU-12 | Como **recepcionista**, quiero que el sistema me impida agendar dos citas con el mismo médico a la misma hora | Alta |
| HU-13 | Como **recepcionista**, quiero cancelar o reprogramar una cita existente | Alta |
| HU-14 | Como **médico**, quiero consultar mi agenda del día para preparar mis consultas | Alta |
| HU-15 | Como **recepcionista**, quiero buscar pacientes por nombre o DUI sin recargar la página | Media |

### Fase 3 — Servicios REST e integración de clientes

| ID | Historia de usuario | Prioridad |
|----|---------------------|-----------|
| HU-16 | Como **paciente**, quiero consultar mis próximas citas desde un portal externo | Alta |
| HU-17 | Como **sistema externo**, quiero consumir la API de disponibilidad de médicos | Media |

### Fase 4 — Spring, calidad y liberación

| ID | Historia de usuario | Prioridad |
|----|---------------------|-----------|
| HU-18 | Como **usuario**, quiero iniciar sesión con mi cuenta para acceder según mi perfil | Alta |
| HU-19 | Como **administrador**, quiero que cada perfil vea únicamente las funciones que le corresponden | Alta |
| HU-20 | Como **administrador**, quiero que el sistema esté desplegado en un entorno accesible desde internet | Alta |

---

## Registro de incidencias

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-01 | `maven-war-plugin` 2.3 incompatible con JDK 21 | Resuelta | Se fijó la versión 3.4.0 en el `pom.xml` |
| INC-02 | El proyecto ubicado en OneDrive bloquea archivos y rompe `mvn clean` | Resuelta | Reubicado en `C:\proyectos` |
| INC-03 | Archivos de plantilla JAX-RS impedían la compilación | Resuelta | Eliminados del proyecto |
| INC-04 | Nombre de base inconsistente: el script usa `clinica`, la configuración apuntaba a `clinica_db` | Resuelta | Unificado a `clinica` |
| INC-05 | `Paciente.setDireccion()` con cuerpo autogenerado que lanzaba `UnsupportedOperationException` | Resuelta | Implementados atributo y accesores reales |
| INC-06 | Los DAO capturaban la excepción y devolvían lista vacía; un fallo de conexión se mostraba como "sin registros" | Resuelta | Los DAO ahora propagan `SQLException` al controlador |
| INC-07 | `listar.jsp` mostraba datos de ejemplo escritos a mano en lugar de leer la base | Resuelta | Implementado `c:forEach` sobre la lista del request |
| INC-08 | Los Servlets buscaban `listado.jsp` mientras los archivos se llaman `listar.jsp` | Resuelta | Corregidas las rutas de `getRequestDispatcher` |
| INC-09 | Enlaces internos sin `contextPath` y apuntando a `conexion.jsp`, que no existe | Resuelta | Todos los enlaces reescritos con `${pageContext.request.contextPath}` |
| INC-10 | Las tildes se corrompían al importar el script desde PowerShell | Resuelta | Importación con `--default-character-set=utf8mb4` y `source` |
| INC-11 | El formulario de médicos pedía el identificador numérico de la especialidad | Resuelta | Reemplazado por lista desplegable cargada desde la base |
| INC-12 | Al guardar un DUI duplicado el sistema regresaba al listado sin avisar | Resuelta | Mensaje de error específico en ambos módulos |
| INC-13 | Solo existía validación de cliente mediante el atributo `required` | Resuelta | Agregada validación de campos obligatorios en el servidor |

---

## Deuda técnica

Elementos que funcionan correctamente pero deben mejorarse en la Fase 2.

| # | Descripción | Prioridad |
|---|-------------|-----------|
| DT-01 | En el modelo `Medico` el atributo se llama `jvpm` mientras la columna de la base es `dui`. El DAO realiza el mapeo, pero conviene unificar el nombre | Media |
| DT-02 | El `doGet` de `PacienteServlet` captura las excepciones e imprime en consola sin informar al usuario | Media |
| DT-03 | `PruebaConexionServlet` fue una herramienta de diagnóstico inicial y ya no es necesaria | Baja |
| DT-04 | Las vistas `editar.jsp` quedaron sin uso: `formulario.jsp` atiende tanto la creación como la edición | Baja |
| DT-05 | La contraseña de base de datos utilizada en desarrollo es débil; debe robustecerse antes del despliegue de la Fase 4 | Media |
