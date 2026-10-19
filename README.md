# Sistema de Gestión de Citas — Clínica

Proyecto de cátedra de **DWF901 — Desarrollo de Aplicaciones con Web Frameworks**
Universidad Don Bosco · Ciclo II 2026

Aplicación Java Web para administrar pacientes, médicos y citas de una clínica.
Se construye por incrementos a lo largo del ciclo: arquitectura MVC con JDBC en la
Fase 1, persistencia con JPA e interfaz JSF en la Fase 2, servicios REST en la
Fase 3 y seguridad por perfiles en la Fase 4.

---

## Integrantes

| # | Nombre | Responsabilidad en Fase 1 | Responsabilidad en Fase 2 |
|---|--------|---------------------------|---------------------------|
| 1 | Dani | Vistas JSP y JSTL | Vistas JSF y plantilla AdminLTE |
| 2 | Levi | Servlets controladores | Managed Beans |
| 3 | Marylin | Clases DAO y acceso a datos | Entidades JPA, validadores e inicio de sesión |
| 4 | Navarro | Diseño de base de datos y script SQL | Tabla de citas y datos de prueba |
| 5 | Fernando | Arquitectura, POJOs, integración, Git y documentación | Arquitectura, reglas de negocio, integración, Git y documentación |

---

## Requisitos

| Herramienta | Versión | Nota |
|-------------|---------|------|
| JDK | 21 (LTS) | Eclipse Adoptium / Temurin |
| Apache NetBeans | 31 | Cualquier IDE con soporte Maven funciona |
| Apache Tomcat | **9.0.121** | Usa el paquete `javax.servlet` |
| MySQL Server | 8.4.11 (LTS) | |
| Maven | Incluido en NetBeans | |
| Hibernate | 5.6.15.Final | Se descarga solo con Maven |
| Mojarra (JSF) | 2.2.20 | Se descarga solo con Maven |
| PrimeFaces | 8.0 | Se descarga solo con Maven |
| AdminLTE | 3.2 | Se carga desde CDN, no requiere instalación |

> **El proyecto usa `javax.servlet` y `javax.persistence`, no `jakarta`.**
> Tomcat 10 o superior **no** ejecutará esta aplicación, y Hibernate 6 tampoco es
> compatible: usa el espacio de nombres `jakarta.persistence`.

### Por qué estas versiones y no otras

Las versiones no son intercambiables. Las tres principales están fijadas por el
servidor sobre el que se despliega:

- **Hibernate 5.6** es la última rama que utiliza `javax.persistence`.
- **Mojarra 2.2** se usa porque JSF 2.3 exige un contenedor CDI, que Tomcat no
  incluye por ser un contenedor de servlets y no un servidor Java EE completo.
  Con la versión 2.3 el despliegue falla con `Unable to find CDI BeanManager`.
- **PrimeFaces 8** se usa porque las versiones posteriores invocan métodos de la
  API de JSF 2.3 que no existen en JSF 2.2.

Además, el `pom.xml` excluye del archivo WAR las bibliotecas `cdi-api`,
`javax.inject` y `jakarta.inject`, que algunas dependencias arrastran de forma
transitiva y que impiden el arranque de JSF sobre Tomcat.

**Antes de cambiar cualquiera de estas versiones, leer esta sección.** Los errores
que produce una incompatibilidad de este tipo no señalan la causa real.

---

## Antes de empezar

**No coloques el proyecto dentro de OneDrive, Google Drive ni Dropbox.**
La sincronización bloquea archivos mientras Maven compila y produce errores como
`Failed to delete ...\target\...` que parecen problemas de código pero no lo son.

Ubicación recomendada: `C:\proyectos\clinica-citas`
Tomcat en una ruta sin espacios ni tildes: `C:\tomcat9`

**Si tienes XAMPP instalado, apaga su MySQL antes de ejecutar el proyecto.**
XAMPP trae su propio servidor MySQL que ocupa el mismo puerto 3306 y no contiene la
base `clinica`. Ver la sección de solución de problemas.

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/Fercho0409/clinica-citas-dwf901.git
cd clinica-citas-dwf901
```

### 2. Verificar que el MySQL correcto esté activo

Antes de cargar la base, confirmar qué servidor está escuchando en el puerto 3306:

```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 3306 -State Listen).OwningProcess | Select-Object ProcessName, Path
```

La ruta debe apuntar a `C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe`.
Si apunta a una carpeta de XAMPP, seguir los pasos de la sección de problemas frecuentes.

### 3. Crear y cargar la base de datos

La base se llama **`clinica`** y las tablas están en plural: `pacientes`, `medicos`,
`especialidades`, `citas` y `usuarios`.

El script `clinicafinal.sql` crea la base, las tablas y carga datos de prueba
(5 especialidades, 10 pacientes, 5 médicos, 12 citas y el usuario administrador).

> **El script comienza con `DROP DATABASE IF EXISTS clinica`.**
> Ejecutarlo borra todo lo que haya en la base, incluidos los registros creados
> durante las pruebas.

**Importante — cómo importarlo sin dañar las tildes:**

```bash
mysql -u root -p --default-character-set=utf8mb4 -e "source C:/ruta/completa/clinicafinal.sql"
```

Con **barras hacia adelante** en la ruta.

Si se importa de otra forma desde PowerShell, los acentos se corrompen y los nombres
aparecen como `Mart?nez`. El archivo está bien; el problema es la codificación de la
consola de Windows.

Para verificar que quedó correcto:

```sql
SELECT HEX(nombres) FROM clinica.pacientes WHERE id_paciente = 3;
```

Debe devolver `4A6F73C3A9` (los bytes `C3A9` son la `é` en UTF-8).

### 4. Configurar las credenciales

Los datos de conexión están en `src/main/resources/META-INF/persistence.xml`.
Si la contraseña local de MySQL es distinta, editarla ahí:

```xml
<property name="javax.persistence.jdbc.user" value="root"/>
<property name="javax.persistence.jdbc.password" value="..."/>
```

> **Esto es deuda técnica conocida.** En la Fase 1 las credenciales vivían en un
> `db.properties` excluido del control de versiones. Al rehacer la configuración de
> JPA quedaron escritas dentro del `persistence.xml`, que sí se versiona. Está
> registrado como DT-07 en el Product Backlog y debe corregirse antes de la Fase 4.

### 5. Configurar el IDE

1. Registrar el JDK 21 en **Tools → Java Platforms → Add Platform**
2. Registrar Tomcat 9 en la pestaña **Services** (usuario y contraseña: `admin` / `admin`)
3. En **Properties → Build → Compile**, seleccionar **JDK 21** como Java Platform

### 6. Ejecutar

Clic derecho en el proyecto → **Clean and Build**, luego `F6`.

La primera compilación tarda más de lo normal porque Maven descarga Hibernate, JSF
y PrimeFaces con sus dependencias.

La aplicación abre en `http://localhost:8080/clinica-citas/` y redirige al inicio de
sesión. Todas las pantallas están protegidas por un filtro: sin sesión iniciada no se
accede a ninguna.

**Credenciales iniciales**

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin@clinica.com` | `admin123` | ADMIN |

Una vez dentro:

- Pacientes: `http://localhost:8080/clinica-citas/views/pacientes/listado.xhtml`
- Médicos: `http://localhost:8080/clinica-citas/views/medicos/listado.xhtml`
- Citas: `http://localhost:8080/clinica-citas/views/citas/listado.xhtml`

> Si la tabla `usuarios` está vacía no es posible iniciar sesión, y como el filtro
> protege todas las rutas, **ninguna pantalla resulta accesible**. El script de base
> de datos ya crea el usuario administrador; si se cargó una versión anterior del
> script, volver a importarlo.

---

## Estructura del proyecto

```
src/main/java/sv/edu/udb/clinica/
├── modelo/         Cita, Especialidad, EstadoCita, Medico, Paciente
│                   Entidades JPA
├── dao/            CitaDAO, EspecialidadDAO, MedicoDAO, PacienteDAO, UsuarioDAO
│                   Acceso a datos con JPQL
├── controlador/    CitaBean, MedicoBean, PacienteBean    Managed Beans
│                   MedicoServlet, PacienteServlet        Fase 1, sin uso
│                   PruebaConexionServlet, PruebaJPAServlet  diagnóstico
├── controller/     LoginBean    (ver deuda técnica DT-10)
├── entity/         Usuario      (ver deuda técnica DT-11)
├── filter/         AuthorizationFilter    protege todas las rutas
├── validator/      DuiValidator, EmailValidator,
│                   FechaNacimientoValidator, TelefonoValidator
└── util/           JPAUtil                fábrica de EntityManager
                    EspecialidadConverter  conversor JSF
                    ConexionBD             Fase 1, sin uso

src/main/resources/
└── META-INF/
    └── persistence.xml      Unidad de persistencia

src/main/webapp/
├── login.xhtml
├── WEB-INF/templates/
│   └── layout.xhtml         Plantilla común (AdminLTE)
└── views/
    ├── pacientes/listado.xhtml
    ├── medicos/listado.xhtml
    └── citas/listado.xhtml

clinicafinal.sql    Script de creación y datos de prueba
docs/               Product Backlog y documentación del proyecto
```

### Separación de responsabilidades

- Las **vistas** solo muestran información. No acceden a datos.
- Los **Managed Beans** reciben las acciones del usuario, aplican las reglas de
  negocio y coordinan. No escriben consultas.
- La **capa de acceso a datos** es la única que consulta la base, mediante JPQL con
  parámetros nombrados.
- Las **entidades** representan las tablas y sus relaciones.

### Configuración de la persistencia

La unidad de persistencia usa `RESOURCE_LOCAL` porque Tomcat no administra
transacciones. Cada operación de escritura abre su propia transacción y la deshace
si algo falla.

El `persistence.xml` declara una sola clase, pero las cinco entidades funcionan:
Hibernate examina por su cuenta el directorio donde está el propio `persistence.xml`
y reconoce todas las clases marcadas con `@Entity`.

La propiedad `hibernate.hbm2ddl.auto` está en `update`, lo que significa que
Hibernate **puede modificar el esquema** al arrancar. Para un entorno de entrega es
preferible `validate`, que solo verifica que las entidades coincidan con las tablas
sin alterar nada. Registrado como DT-08.

### Relaciones entre entidades

Las uniones ya no se escriben a mano. La entidad `Cita` declara sus relaciones:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_paciente", nullable = false)
private Paciente paciente;
```

La carga diferida evita traer datos que quizá no se usen, pero puede provocar una
consulta por cada fila al recorrer una lista. Por eso las consultas de listado piden
las relaciones resueltas desde el inicio con `JOIN FETCH`.

---

## Funcionalidad implementada

**Pacientes** — listar, crear, editar, eliminar, con búsqueda AJAX por nombre o DUI.

**Médicos** — listar, crear, editar, eliminar, con selección de especialidad desde
lista desplegable y búsqueda AJAX.

**Citas** — agendar, editar, marcar como atendida y cancelar, con filtros AJAX por
texto, médico y estado.

**Reglas de negocio del agendamiento**

- Un médico no puede tener dos citas vigentes en la misma fecha y hora.
- Solo las citas en estado `PROGRAMADA` o `REPROGRAMADA` ocupan el horario; al
  cancelar una cita, el horario vuelve a quedar disponible.
- No se permite agendar en una fecha u hora ya transcurrida.
- Cancelar **no** elimina la cita: cambia su estado y conserva el historial.

**Validadores**

| Validador | Comprueba |
|-----------|-----------|
| `duiValidator` | Formato del documento y su dígito verificador |
| `emailValidator` | Dirección de correo bien formada |
| `telefonoValidator` | Formato del número telefónico |
| `fechaNacimientoValidator` | Fecha coherente, ni futura ni excesivamente antigua |

**Control de acceso** — inicio de sesión con usuario y contraseña. El filtro
`AuthorizationFilter` intercepta todas las peticiones y redirige al login cuando no
hay sesión iniciada, de modo que una pantalla nueva queda protegida sin necesidad de
agregarle nada.

---

## Convenciones de trabajo

- La rama `main` se mantiene siempre estable.
- Cada tarea se desarrolla en su propia rama: `feature/dao-paciente`, `feature/vista-medico`.
- Los cambios entran a `main` mediante Pull Request.
- Commits descriptivos en español, en imperativo.
- Cada integrante sube su propio trabajo. El historial de commits es parte de la evaluación.

Antes del primer commit, configurar la identidad con el correo de la cuenta de GitHub:

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo@ejemplo.com"
```

Si el correo no coincide, los commits no se acreditan al autor.

> **Al trabajar en ramas, `git branch` solo muestra las locales.** Para ver el
> trabajo que los demás ya publicaron hay que descargar primero las referencias
> remotas:
>
> ```bash
> git fetch --all
> git branch -r
> ```
>
> Sin ese `fetch`, el trabajo de los compañeros parece no existir.

---

## Solución de problemas frecuentes

### Entorno y base de datos

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `Access denied for user 'root'@'localhost'` | El MySQL de XAMPP ocupa el puerto 3306 | Ver la sección siguiente |
| `Failed to delete ...\target\...` | El proyecto está en OneDrive | Mover a `C:\proyectos` |
| Nombres con `?` o caracteres raros | Script importado sin `--default-character-set=utf8mb4` | Reimportar con el comando de la sección 3 |
| `Public Key Retrieval is not allowed` | Configuración de MySQL 8.4 | Ya incluido en la URL del `persistence.xml` |
| `Unable to create requested service [JdbcEnvironment]` | Hibernate no logró conectar | El error real está al final del seguimiento, normalmente es la contraseña o el puerto |
| Ninguna pantalla carga, todo redirige al login | La tabla `usuarios` está vacía | Reimportar el script de base de datos |

### Arranque de JSF y PrimeFaces

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `Unable to find CDI BeanManager` | Mojarra 2.3 requiere CDI, que Tomcat no tiene | Fijar Mojarra en 2.2.20 |
| `NoSuchMethodError: Application.addSearchKeywordResolver` | PrimeFaces 12 invoca métodos de JSF 2.3 | Fijar PrimeFaces en 8.0 |
| `The entity "copy" was referenced, but not declared` | Facelets procesa XML estricto | Usar `&#169;` en lugar de `&copy;` |
| Los botones aparecen sin icono | La hoja de estilos de PrimeIcons no se carga | Usar iconos de Font Awesome (`fas fa-...`), que ya trae AdminLTE |

### Vistas y persistencia

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `The button/link/text component needs to have a Form in its ancestry` | El `</h:form>` cierra antes de la tabla | Mover el cierre después de `</p:dataTable>` |
| `Cannot find component for expression "..."` | Identificador relativo dentro de un `p:dataTable`, que es un contenedor de nombres propio | Usar identificadores absolutos: `:formCitas:dt-citas` |
| `No se ha podido convertir ... en una cadena` | `f:convertDateTime type="localDate"` solo existe en JSF 2.3 | Exponer la fecha ya formateada desde la entidad |
| `wrong column type ...; found [int], but expecting [bigint]` | El tipo del identificador en la entidad no coincide con la columna | Usar `Integer` cuando la columna es `INT` |
| `cannot find symbol: method setXxx` tras renombrar un atributo | Quedaron referencias al nombre anterior | Buscar y reemplazar **con distinción de mayúsculas activada** |

### Conflicto con XAMPP

XAMPP incluye su propio servidor MySQL, que ocupa el puerto 3306 y no contiene la base
`clinica`. Su usuario `root` no tiene contraseña, así que la aplicación recibe
`Access denied for user 'root'@'localhost' (using password: YES)`.

El proceso de XAMPP puede seguir activo aunque su panel de control lo muestre detenido.

**1. Identificar qué proceso ocupa el puerto:**

```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 3306 -State Listen).OwningProcess | Select-Object Id, ProcessName, Path
```

**2. Si la ruta apunta a XAMPP, detenerlo** desde su panel de control. Si el panel ya lo
muestra apagado, terminar el proceso con el identificador que devolvió el comando anterior:

```powershell
Stop-Process -Id <ID> -Force
```

**3. Iniciar el servicio correcto**, en PowerShell **como administrador**:

```powershell
Start-Service MySQL84
```

**4. Confirmar** repitiendo el comando del paso 1. La ruta debe ser
`C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe`.

Para usar ambos servidores a la vez, cambiar el puerto del MySQL de XAMPP a 3307 en su
archivo `my.ini`.

---

## Deuda técnica

Pendientes conocidos. El detalle completo está en `docs/product-backlog.md`.

- Las contraseñas de los usuarios se guardan en texto plano.
- Las credenciales de la base están escritas dentro del `persistence.xml`, que se
  versiona. En la Fase 1 vivían en un archivo externo excluido del repositorio.
- `hibernate.hbm2ddl.auto` está en `update`: Hibernate puede modificar el esquema al
  arrancar.
- `LoginBean` quedó en un paquete `controller` mientras los demás controladores están
  en `controlador`.
- `Usuario` está en el paquete `entity` y no en `modelo`, y su archivo fuente se
  encuentra en una carpeta que no corresponde al paquete que declara.
- Permanece código de la Fase 1 que ya nadie invoca: `ConexionBD`, `MedicoServlet`,
  `PacienteServlet`, `PruebaConexionServlet` y `PruebaJPAServlet`.
- Los DUI del script de datos de prueba no superan la validación del dígito
  verificador, por lo que esos registros no pueden reeditarse sin corregir el documento.
- HU-14 (agenda del día del médico) tiene la consulta implementada en `CitaDAO` pero
  no cuenta con pantalla.

---

## Estado

**Fase 1 — Fundamentos Java Web y Arquitectura MVC** · Entregada
Septiembre de 2026 · 20% de la nota

- [x] Configuración del proyecto y repositorio
- [x] Base de datos MySQL y script de creación
- [x] POJOs del modelo
- [x] Conexión con configuración externa
- [x] DAOs con consultas parametrizadas
- [x] Servlets controladores
- [x] Vistas JSP con JSTL
- [x] CRUD completo de pacientes y médicos
- [x] Validaciones de cliente y servidor
- [x] PDF de evidencias

**Fase 2 — Persistencia Empresarial e Integración JSF** · Entregada
18 de octubre de 2026 · 25% de la nota

- [x] Configuración de Hibernate y la unidad de persistencia
- [x] Entidades JPA con sus relaciones
- [x] Operaciones CRUD con JPA y transacciones
- [x] Tabla de citas y datos de prueba
- [x] Managed Beans
- [x] Vistas JSF sobre AdminLTE
- [x] Reglas de negocio del agendamiento
- [x] Validadores, convertidores y operaciones AJAX
- [x] Control de acceso mediante inicio de sesión
- [x] PDF de evidencias
- [ ] HU-14: pantalla de agenda diaria del médico

### Alcance

La Fase 1 cubrió los módulos de **pacientes** y **médicos** con JDBC.
La Fase 2 incorpora el módulo de **citas**, migra la persistencia a JPA, implementa
la interfaz con JSF sobre la plantilla AdminLTE —solicitada durante la defensa
anterior— y agrega control de acceso.
