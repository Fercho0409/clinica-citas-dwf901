# Product Backlog — Sistema de Gestión de Citas

**Proyecto:** Clínica — DWF901, Universidad Don Bosco
**Repositorio:** https://github.com/Fercho0409/clinica-citas-dwf901
**Última actualización:** 1 de octubre de 2026

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
| HT-10 | Elaborar el PDF de evidencias | Integrante 5 | Completada |

---

## Observaciones del Sprint Review I

Solicitudes de cambio recibidas durante la defensa de la Fase 1. Se incorporan al incremento de la Fase 2.

| # | Observación | Origen | Estado |
|---|-------------|--------|--------|
| SR-01 | La apariencia de las vistas no resulta adecuada. Se solicita emplear la plantilla AdminLTE | Docente | En proceso |

---

## Fase 2 — Sprint II: Persistencia Empresarial e Integración JSF

**Entrega:** 10 u 11 de octubre de 2026 · **Ponderación:** 25%

### Historias de usuario

| ID | Historia de usuario | Prioridad | Estado |
|----|---------------------|-----------|--------|
| HU-11 | Como **recepcionista**, quiero agendar una cita seleccionando paciente, médico, fecha y hora | Alta | Pendiente |
| HU-12 | Como **recepcionista**, quiero que el sistema me impida agendar dos citas con el mismo médico a la misma hora | Alta | Pendiente |
| HU-13 | Como **recepcionista**, quiero cancelar o reprogramar una cita existente | Alta | Pendiente |
| HU-14 | Como **médico**, quiero consultar mi agenda del día para preparar mis consultas | Alta | Pendiente |
| HU-15 | Como **recepcionista**, quiero buscar pacientes por nombre o DUI sin recargar la página | Media | Pendiente |

**Criterios de aceptación de HU-12**
- Al intentar agendar en un horario ya ocupado por ese médico, el sistema rechaza la operación
- El mensaje indica con qué cita existente hay conflicto
- La validación se ejecuta en el servidor, no únicamente en el navegador
- No se permite agendar en una fecha u hora ya transcurrida

### Tareas técnicas

| ID | Tarea | Responsable | Estado |
|----|-------|-------------|--------|
| HT-11 | Configurar Hibernate como proveedor de JPA y la unidad de persistencia | Integrante 5 | Completada |
| HT-12 | Adaptar la lectura de credenciales externas al arranque de JPA | Integrante 5 | Completada |
| HT-13 | Convertir los POJOs en entidades JPA con sus anotaciones y relaciones | Por asignar | Pendiente |
| HT-14 | Reimplementar las operaciones CRUD con JPA y gestión de transacciones | Por asignar | Pendiente |
| HT-15 | Diseñar la tabla de citas, sus relaciones y datos de prueba | Por asignar | Pendiente |
| HT-16 | Implementar los Managed Beans de pacientes, médicos y citas | Por asignar | Pendiente |
| HT-17 | Construir las vistas JSF sobre la plantilla AdminLTE | Por asignar | Pendiente |
| HT-18 | Implementar las reglas de negocio del agendamiento de citas | Integrante 5 | Pendiente |
| HT-19 | Implementar validadores, convertidores y operaciones AJAX | Por asignar | Pendiente |
| HT-20 | Actualizar README, script de base de datos y Product Backlog | Integrante 5 | En progreso |
| HT-21 | Elaborar el PDF de evidencias de la Fase 2 | Por asignar | Pendiente |

---

## Fases posteriores

Historias identificadas, fuera del alcance del Sprint II. Se detallarán al inicio de cada fase.

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

### Fase 1

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

### Fase 2

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-14 | Con XAMPP instalado, su servidor MySQL ocupa el puerto 3306 y la aplicación recibe "Access denied for user root". El proceso puede quedar activo aunque el panel de XAMPP lo muestre detenido | Resuelta | Verificar qué proceso escucha en el puerto 3306, detener el de XAMPP e iniciar el servicio MySQL84. Documentado en el README |

---

## Deuda técnica

| # | Descripción | Prioridad | Estado |
|---|-------------|-----------|--------|
| DT-01 | En el modelo `Medico` el atributo se llama `jvpm` mientras la columna de la base es `dui`. El DAO realiza el mapeo, pero conviene unificar el nombre | Media | Se resuelve al convertir `Medico` en entidad (HT-13) |
| DT-02 | El `doGet` de `PacienteServlet` captura las excepciones e imprime en consola sin informar al usuario | Media | Se resuelve al migrar a Managed Beans (HT-16) |
| DT-03 | `PruebaConexionServlet` fue una herramienta de diagnóstico inicial y ya no es necesaria | Baja | Se elimina al cerrar la Fase 2, junto con `PruebaJPAServlet` |
| DT-04 | Las vistas `editar.jsp` quedaron sin uso: `formulario.jsp` atiende tanto la creación como la edición | Baja | Se resuelve al rehacer las vistas con JSF (HT-17) |
| DT-05 | La contraseña de base de datos utilizada en desarrollo es débil; debe robustecerse antes del despliegue de la Fase 4 | Media | Pendiente para la Fase 4 |
| DT-06 | `PruebaJPAServlet` es una herramienta temporal de verificación de la configuración de persistencia | Baja | Se elimina al cerrar la Fase 2 |
