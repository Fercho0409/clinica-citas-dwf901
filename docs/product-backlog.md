# Product Backlog — Sistema de Gestión de Citas

**Proyecto:** Clínica — DWF901, Universidad Don Bosco
**Repositorio:** https://github.com/Fercho0409/clinica-citas-dwf901
**Última actualización:** 18 de octubre de 2026

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
| SR-01 | La apariencia de las vistas no resulta adecuada. Se solicita emplear la plantilla AdminLTE | Docente | Atendida |

---

## Fase 2 — Sprint II: Persistencia Empresarial e Integración JSF

**Entrega:** 18 de octubre de 2026 · **Ponderación:** 25%

### Historias de usuario

| ID | Historia de usuario | Prioridad | Estado |
|----|---------------------|-----------|--------|
| HU-11 | Como **recepcionista**, quiero agendar una cita seleccionando paciente, médico, fecha y hora | Alta | Completada |
| HU-12 | Como **recepcionista**, quiero que el sistema me impida agendar dos citas con el mismo médico a la misma hora | Alta | Completada |
| HU-13 | Como **recepcionista**, quiero cancelar o reprogramar una cita existente | Alta | Completada |
| HU-14 | Como **médico**, quiero consultar mi agenda del día para preparar mis consultas | Alta | Parcial |
| HU-15 | Como **recepcionista**, quiero buscar pacientes por nombre o DUI sin recargar la página | Media | Completada |

**Criterios de aceptación de HU-12**

| Criterio | Cumplido |
|----------|----------|
| Al intentar agendar en un horario ya ocupado por ese médico, el sistema rechaza la operación | Sí |
| El mensaje indica con qué cita existente hay conflicto | Sí — se indican fecha y hora |
| La validación se ejecuta en el servidor, no únicamente en el navegador | Sí — en `CitaBean.guardar()` |
| No se permite agendar en una fecha u hora ya transcurrida | Sí |

**Criterios de aceptación de HU-13**

| Criterio | Cumplido |
|----------|----------|
| Una cita puede cancelarse sin eliminarse de la base | Sí — pasa a estado `CANCELADA` |
| Una cita cancelada libera el horario del médico | Sí — solo `PROGRAMADA` y `REPROGRAMADA` lo ocupan |
| Una cita puede marcarse como atendida | Sí |
| La acción solicita confirmación antes de ejecutarse | Sí |

**Estado de HU-14**

La consulta `CitaDAO.agendaDelDia(idMedico, fecha)` está implementada y probada, pero
no se construyó la pantalla que la invoca. La historia queda abierta para la Fase 3.

### Tareas técnicas

| ID | Tarea | Responsable | Estado |
|----|-------|-------------|--------|
| HT-11 | Configurar Hibernate como proveedor de JPA y la unidad de persistencia | Integrante 5 | Completada |
| HT-12 | Adaptar la lectura de credenciales al arranque de JPA | Integrante 5 | Revertida — ver DT-07 |
| HT-13 | Convertir los POJOs en entidades JPA con sus anotaciones y relaciones | Integrantes 3 y 5 | Completada |
| HT-14 | Reimplementar las operaciones CRUD con JPA y gestión de transacciones | Integrantes 3 y 5 | Completada |
| HT-15 | Diseñar la tabla de citas, sus relaciones y datos de prueba | Integrante 4 | Completada |
| HT-16 | Implementar los Managed Beans de pacientes, médicos y citas | Integrantes 2 y 5 | Completada |
| HT-17 | Construir las vistas JSF sobre la plantilla AdminLTE | Integrante 1 | Completada |
| HT-18 | Implementar las reglas de negocio del agendamiento de citas | Integrante 5 | Completada |
| HT-19 | Implementar validadores, convertidores y operaciones AJAX | Integrante 3 | Completada |
| HT-20 | Actualizar README, script de base de datos y Product Backlog | Integrante 5 | Completada |
| HT-21 | Elaborar el PDF de evidencias de la Fase 2 | Integrante 5 | Completada |
| HT-22 | Implementar el inicio de sesión y el filtro de control de acceso | Integrante 3 | Completada |
| HT-23 | Fijar las versiones compatibles de JSF, PrimeFaces e Hibernate para Tomcat 9 | Integrante 5 | Completada |
| HT-24 | Integrar el trabajo de las ramas del equipo en la rama principal | Integrante 5 | Completada |
| HT-25 | Construir la pantalla de agenda diaria del médico (HU-14) | Por asignar | Pendiente |

---

## Fases posteriores

Historias identificadas, fuera del alcance del Sprint II. Se detallarán al inicio de cada fase.

### Fase 3 — Servicios REST e integración de clientes

| ID | Historia de usuario | Prioridad |
|----|---------------------|-----------|
| HU-14 | Como **médico**, quiero consultar mi agenda del día para preparar mis consultas | Alta |
| HU-16 | Como **paciente**, quiero consultar mis próximas citas desde un portal externo | Alta |
| HU-17 | Como **sistema externo**, quiero consumir la API de disponibilidad de médicos | Media |

### Fase 4 — Spring, calidad y liberación

| ID | Historia de usuario | Prioridad |
|----|---------------------|-----------|
| HU-18 | Como **usuario**, quiero iniciar sesión con mi cuenta para acceder según mi perfil | Alta |
| HU-19 | Como **administrador**, quiero que cada perfil vea únicamente las funciones que le corresponden | Alta |
| HU-20 | Como **administrador**, quiero que el sistema esté desplegado en un entorno accesible desde internet | Alta |

> HU-18 quedó cubierta de forma anticipada en la Fase 2 con el inicio de sesión y el
> filtro de control de acceso. En la Fase 4 corresponde reforzarla con cifrado de
> contraseñas y diferenciación efectiva por perfil.

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

### Fase 2 — Entorno y control de versiones

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-14 | Con XAMPP instalado, su servidor MySQL ocupa el puerto 3306 y la aplicación recibe "Access denied for user root". El proceso puede quedar activo aunque el panel de XAMPP lo muestre detenido | Resuelta | Verificar qué proceso escucha en el puerto 3306, detener el de XAMPP e iniciar el servicio MySQL84. Documentado en el README |
| INC-15 | El trabajo de varios integrantes no aparecía al consultar las ramas del repositorio | Resuelta | `git branch` solo lista ramas locales. Tras `git fetch --all`, `git branch -r` reveló las ramas remotas, que se integraron a `main` |
| INC-16 | Un integrante entregó el módulo compilado en lugar del código fuente | Resuelta | Se recuperó la estructura de las clases con `javap` y se reescribieron los fuentes. Se confirmó que ese módulo seguía usando JDBC y no entidades JPA |

### Fase 2 — Compatibilidad de versiones

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-17 | `javax.faces.FacesException: Unable to find CDI BeanManager` al desplegar | Resuelta | JSF 2.3 requiere un contenedor CDI del que Tomcat carece. Se fijó Mojarra en 2.2.20 |
| INC-18 | `NoSuchMethodError: Application.addSearchKeywordResolver` al abrir la primera pantalla | Resuelta | PrimeFaces 12 invoca métodos de JSF 2.3. Se fijó PrimeFaces en 8.0 |
| INC-19 | Bibliotecas `cdi-api`, `javax.inject` y `jakarta.inject` arrastradas de forma transitiva impedían el arranque de JSF | Resuelta | Excluidas del WAR mediante `packagingExcludes` en el `pom.xml` |

### Fase 2 — Entidades y persistencia

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-20 | `Schema-validation: wrong column type encountered in column [id_medico]; found [int], but expecting [bigint]` | Resuelta | El identificador estaba declarado como `Long` y la columna es `INT`. Cambiado a `Integer` en `Medico` y `Especialidad` |
| INC-21 | Hibernate no encontraba las columnas `nombre` y `apellido` de la tabla `medicos` | Resuelta | Las anotaciones `@Column` usaban singular y la tabla define los nombres en plural |
| INC-22 | Error de compilación `cannot find symbol: method setJvpm` tras unificar el modelo | Resuelta | Quedaron referencias al nombre anterior del atributo. Se reemplazaron activando la distinción de mayúsculas en la búsqueda |

### Fase 2 — Vistas JSF

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-23 | `Error Parsing layout.xhtml: The entity "copy" was referenced, but not declared` | Resuelta | Facelets procesa XML estricto. Se sustituyó `&copy;` por `&#169;` |
| INC-24 | `The button/link/text component needs to have a Form in its ancestry` | Resuelta | El cierre del formulario quedaba antes de la tabla. Se movió después de `</p:dataTable>` en las tres vistas |
| INC-25 | `No se ha podido convertir '1999-09-25' en una cadena` | Resuelta | El conversor de fechas de JSF 2.2 no admite `LocalDate`. Se expuso la fecha ya formateada desde la entidad |
| INC-26 | El formulario de citas no mostraba los campos de fecha y hora, pero exigía llenarlos al guardar | Resuelta | La vista no incluía los componentes correspondientes. Se agregaron `p:datePicker` y un campo de hora interpretado por el bean |
| INC-27 | `Cannot find component for expression "messages"` al usar los botones de la tabla | Resuelta | Los identificadores relativos no se resuelven dentro de un `p:dataTable`, que es un contenedor de nombres propio. Se usaron identificadores absolutos |
| INC-28 | Los botones de acción se mostraban sin icono | Resuelta | La hoja de estilos de PrimeIcons no se cargaba. Se sustituyeron por iconos de Font Awesome, que ya acompaña a AdminLTE |
| INC-29 | El formulario de médicos no incluía el campo DUI, pero el controlador lo exige y la columna no admite valores vacíos ni repetidos | Resuelta | Se agregó el campo al formulario y la columna correspondiente a la tabla |

### Fase 2 — Control de acceso

| # | Incidencia | Estado | Resolución |
|---|-----------|--------|------------|
| INC-30 | Ninguna pantalla resultaba accesible tras incorporar el inicio de sesión | Resuelta | La tabla `usuarios` estaba vacía, de modo que era imposible autenticarse, y el filtro redirigía todas las peticiones al login. Se agregó al script la creación de la tabla y el usuario administrador |

---

## Deuda técnica

| # | Descripción | Prioridad | Estado |
|---|-------------|-----------|--------|
| DT-01 | En el modelo `Medico` el atributo se llama `jvpm` mientras la columna de la base es `dui` | Media | Resuelta en HT-13 |
| DT-02 | El `doGet` de `PacienteServlet` captura las excepciones e imprime en consola sin informar al usuario | Media | Resuelta al migrar a Managed Beans (HT-16) |
| DT-03 | `PruebaConexionServlet` fue una herramienta de diagnóstico inicial y ya no es necesaria | Baja | Pendiente |
| DT-04 | Las vistas `editar.jsp` quedaron sin uso: `formulario.jsp` atiende tanto la creación como la edición | Baja | Resuelta al rehacer las vistas con JSF (HT-17) |
| DT-05 | La contraseña de base de datos utilizada en desarrollo es débil | Media | Pendiente para la Fase 4 |
| DT-06 | `PruebaJPAServlet` es una herramienta temporal de verificación de la configuración de persistencia | Baja | Pendiente |
| DT-07 | Las credenciales de la base quedaron escritas dentro del `persistence.xml`, que sí se versiona. En la Fase 1 residían en un archivo externo excluido del repositorio | **Alta** | Pendiente — revierte la decisión tomada en HT-02 y HT-12 |
| DT-08 | `hibernate.hbm2ddl.auto` está configurado en `update`, por lo que Hibernate puede modificar el esquema de la base al arrancar la aplicación | **Alta** | Pendiente — debe cambiarse a `validate` |
| DT-09 | Las contraseñas de los usuarios se almacenan en texto plano y se comparan directamente | **Alta** | Pendiente para la Fase 4 |
| DT-10 | `LoginBean` quedó en un paquete `controller` mientras los demás controladores están en `controlador` | Media | Pendiente |
| DT-11 | La entidad `Usuario` está en el paquete `entity` y no en `modelo`, y su archivo fuente se encuentra en una carpeta que no corresponde al paquete que declara | Media | Pendiente |
| DT-12 | Permanece código de la Fase 1 que ya nadie invoca: `ConexionBD`, `MedicoServlet` y `PacienteServlet` | Baja | Pendiente |
| DT-13 | Los números de DUI del script de datos de prueba no superan la validación del dígito verificador, por lo que esos registros no pueden reeditarse sin corregir antes el documento | Media | Pendiente |
| DT-14 | `UsuarioDAO` construye su propio `EntityManagerFactory` en lugar de usar `JPAUtil`, duplicando una operación costosa | Media | Pendiente |

### Prioridades para la Fase 3

El orden sugerido de atención es DT-07, DT-08 y DT-09, por ser los que afectan la
seguridad y la integridad de los datos. Los demás son de orden y limpieza, y pueden
resolverse junto con el trabajo regular de la fase.
