# Fase 4 — Tarea 3: Configuración, errores y limpieza

Responsable: Gustavo Adolfo Navarro Martínez (Integrante 3)

## Qué se hizo

| Deuda | Cambio | Archivos |
|-------|--------|----------|
| DT-07 | Las credenciales salieron del `persistence.xml`. `ConfiguracionBD` las lee de variables de entorno, de un archivo externo (`CLINICA_CONFIG`) o de `db.properties`, que no se versiona. `JPAUtil` se las pasa a JPA al crear la fábrica. | `config/ConfiguracionBD.java`, `util/JPAUtil.java`, `persistence.xml`, `db.properties.example` |
| DT-08 | `hibernate.hbm2ddl.auto` pasó de `update` a `validate`. `show_sql` quedó apagado por defecto (`db.mostrarSql=true` para activarlo). | `persistence.xml` |
| — | Manejo centralizado de errores en las pantallas: `ExceptionHandler` de JSF, página `error.xhtml`, `<error-page>` en `web.xml` y clase `Mensajes` común para todos los Managed Beans. | `error/*`, `controlador/PaginaErrorBean.java`, `error.xhtml`, `faces-config.xml`, `web.xml` |
| DT-03, DT-06, DT-12 | Eliminados `ConexionBD`, `MedicoServlet`, `PacienteServlet`, `PruebaConexionServlet`, `PruebaJPAServlet`, `index.jsp` y las vistas `.jsp` de la Fase 1. | — |
| DT-10 | `LoginBean` pasó de `controller` a `controlador`. | `controlador/LoginBean.java` |
| DT-11 | `Usuario` pasó de `entity` (y una carpeta que no correspondía) a `modelo`. | `modelo/Usuario.java` |
| DT-14 | `UsuarioDAO` usa `JPAUtil` en lugar de su propia fábrica. Además, un fallo de la base ya no se confunde con "usuario o contraseña incorrectos". | `dao/UsuarioDAO.java` |

## Por qué así

**Variables de entorno primero, archivo después.** En el servidor (tarea 5) lo
normal es no dejar archivos con contraseñas dentro de la aplicación desplegada; en
NetBeans lo cómodo es un archivo. Con este orden sirven las dos cosas sin tocar
código. Si falta un dato, la aplicación no arranca y el mensaje dice cuál falta: es
preferible a que arranque y falle en la primera consulta.

**`validate` y no `none`.** `none` no comprueba nada; `validate` detecta al arrancar
que la base no coincide con las entidades, en lugar de fallar más tarde con un error
confuso en una pantalla.

**Los errores imprevistos llevan un código.** Antes los `catch` concatenaban
`ex.getMessage()` en el mensaje al usuario, lo que exponía nombres de tablas y
restricciones. Ahora el detalle completo va al registro con `[ref A1B2C3]` y en
pantalla solo aparece la acción que falló y el código. Es el mismo criterio de la
API (`ManejadorErrores`).

**`error.xhtml` no usa la plantilla.** Si el error viene de la plantilla o de la
sesión, la página de error no debe volver a fallar por lo mismo.

**`JPAUtil` ya no lanza `ExceptionInInitializerError`.** Es un `Error`, no una
`RuntimeException`, así que se saltaba los `catch` de los Managed Beans y dejaba la
pantalla en blanco.

## Cómo probarlo (evidencias)

| # | Caso | Pasos | Resultado esperado |
|---|------|-------|--------------------|
| 1 | Credenciales fuera del repositorio | `git grep -n "jdbc.password"` | Ninguna contraseña en archivos versionados |
| 2 | Falta `db.properties` | Renombrarlo y ejecutar | No arranca; el log dice "Falta configuracion de la base de datos: db.url…" |
| 3 | Variables de entorno | Definir `CLINICA_DB_PASSWORD` en la configuración de Tomcat | Conecta aunque `db.properties` tenga otra contraseña |
| 4 | `validate` no modifica la base | Renombrar una columna en MySQL y ejecutar | No arranca: `Schema-validation: missing column` |
| 5 | Ruta inexistente | Abrir `/clinica-citas/views/noexiste.xhtml` | Página 404 propia, sin rastro de Tomcat |
| 6 | Error inesperado en una acción | Detener MySQL y guardar un paciente | Mensaje "No se pudo guardar el paciente… informe el código XXXXXX"; el log tiene `[ref XXXXXX]` con la excepción completa |
| 7 | Sesión expirada | Bajar `session-timeout` a 1, esperar 2 min y recargar | Con el filtro actual se vuelve al login. `ViewExpiredException` queda cubierta como respaldo por si la vista caduca con la sesión aún activa |
| 8 | Login con la base apagada | Detener MySQL e iniciar sesión | Mensaje de error con código, no "usuario o contraseña incorrectos" |
| 9 | Código eliminado | Abrir `/clinica-citas/prueba-conexion` y `/clinica-citas/pacientes` | 404 |

## Para las otras tareas

- **Tarea 1 (Spring):** si Spring arma el `DataSource` o el `EntityManagerFactory`,
  puede tomar los valores de `ConfiguracionBD.cargar()` para no volver a escribir
  credenciales en un archivo versionado.
- **Tarea 2 (Seguridad):** `/error.xhtml` debe quedar con acceso libre
  (`permitAll`), igual que el login, para que se vea cuando la sesión expiró. La
  página ya contempla el 403 ("Acceso no permitido"). `Usuario` ahora está en
  `sv.edu.udb.clinica.modelo` y `LoginBean` en `sv.edu.udb.clinica.controlador`.
- **Tarea 5 (Despliegue):** en el servidor basta con definir `CLINICA_DB_URL`,
  `CLINICA_DB_USUARIO` y `CLINICA_DB_PASSWORD` (por ejemplo en `setenv.sh` de
  Tomcat). No hace falta `db.properties`.
- **Todos:** después de bajar estos cambios hay que crear
  `src/main/resources/db.properties` a partir del `.example`; sin él la aplicación no
  arranca.

## Pendiente

- DT-05 (contraseña débil de la base de desarrollo) depende de cada máquina; en el
  servidor se debe usar una contraseña fuerte vía `CLINICA_DB_PASSWORD`.
- DT-09 (contraseñas de usuarios en texto plano) es de la tarea 2.
- DT-13 (DUI del script) no forma parte de esta tarea.
