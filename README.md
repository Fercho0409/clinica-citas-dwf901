# Sistema de Gestión de Citas — Clínica

Proyecto de cátedra de **DWF901 — Desarrollo de Aplicaciones con Web Frameworks**
Universidad Don Bosco · Ciclo II 2026

Aplicación Java Web para administrar pacientes, médicos y citas de una clínica.
Se construye por incrementos a lo largo del ciclo: arquitectura MVC con JDBC en la
Fase 1, persistencia con JPA e interfaz JSF en la Fase 2, servicios REST en la
Fase 3 y seguridad por perfiles en la Fase 4.

---

## Integrantes

| # | Nombre | Responsabilidad en Fase 1 |
|---|--------|---------------------------|
| 1 | Dani | Vistas JSP y JSTL |
| 2 | Levi | Servlets controladores |
| 3 | Marylin | Clases DAO y acceso a datos |
| 4 | Navarro | Diseño de base de datos y script SQL |
| 5 | Fernando | Arquitectura, POJOs, integración, Git y documentación |

---

## Requisitos

| Herramienta | Versión | Nota |
|-------------|---------|------|
| JDK | 21 (LTS) | Eclipse Adoptium / Temurin |
| Apache NetBeans | 31 | Cualquier IDE con soporte Maven funciona |
| Apache Tomcat | **9.0.121** | Usa el paquete `javax.servlet` |
| MySQL Server | 8.4.11 (LTS) | |
| Maven | Incluido en NetBeans | |
| Hibernate | 5.6.15.Final | Se descarga solo con Maven, no se instala aparte |

> **El proyecto usa `javax.servlet` y `javax.persistence`, no `jakarta`.**
> Tomcat 10 o superior **no** ejecutará esta aplicación, y Hibernate 6 tampoco es
> compatible: usa el espacio de nombres `jakarta.persistence`.

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
`especialidades`.

El script `clinicafinal.sql` crea la base, las tablas y carga datos de prueba
(5 especialidades, 10 pacientes y 5 médicos).

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

```bash
copy src\main\resources\db.properties.example src\main\resources\db.properties
```

Editar `db.properties` con la contraseña local de MySQL.

> `db.properties` está en `.gitignore` y **no debe subirse al repositorio**.
> Antes de cada `git push`, verificar con `git status` que no aparezca.

Este archivo alimenta tanto la conexión JDBC de la Fase 1 como la configuración de
JPA de la Fase 2. El `persistence.xml` no contiene credenciales.

### 5. Configurar el IDE

1. Registrar el JDK 21 en **Tools → Java Platforms → Add Platform**
2. Registrar Tomcat 9 en la pestaña **Services** (usuario y contraseña: `admin` / `admin`)
3. En **Properties → Build → Compile**, seleccionar **JDK 21** como Java Platform

### 6. Ejecutar

Clic derecho en el proyecto → **Clean and Build**, luego `F6`.

La primera compilación tarda más de lo normal porque Maven descarga Hibernate y sus
dependencias.

- Portada: `http://localhost:8080/clinica-citas/`
- Pacientes: `http://localhost:8080/clinica-citas/pacientes`
- Médicos: `http://localhost:8080/clinica-citas/medicos`

### 7. Verificar la configuración de JPA

```
http://localhost:8080/clinica-citas/prueba-jpa
```

Debe mostrar "Conexión correcta" y listar las cinco especialidades. En la ventana
**Output → Apache Tomcat or TomEE** de NetBeans aparece el SQL que Hibernate genera.

Esta página es temporal y se elimina al cerrar la Fase 2.

---

## Estructura del proyecto

```
src/main/java/sv/edu/udb/clinica/
├── modelo/         Paciente, Medico, Especialidad
│                   (en migración a entidades JPA)
├── dao/            PacienteDAO, MedicoDAO
│                   (JDBC con PreparedStatement, en migración a JPA)
├── controlador/    PacienteServlet, MedicoServlet
└── util/           ConexionBD    conexión JDBC (Fase 1)
                    JPAUtil       fábrica de EntityManager (Fase 2)

src/main/resources/
├── META-INF/
│   └── persistence.xml      Unidad de persistencia (sin credenciales)
├── db.properties            Credenciales locales (NO versionado)
└── db.properties.example    Plantilla de configuración

src/main/webapp/
├── index.jsp
└── views/
    ├── pacientes/      listar, formulario, editar
    ├── medicos/        listar, formulario, editar
    ├── mensajes/       exito, error
    └── partials/       navbar

clinicafinal.sql    Script de creación y datos de prueba
docs/               Product Backlog y documentación del proyecto
```

### Separación de responsabilidades

- Las **vistas** solo muestran información. No acceden a datos.
- Los **controladores** reciben peticiones, validan y coordinan. No escriben SQL.
- La **capa de acceso a datos** es la única que consulta la base, con consultas
  parametrizadas.
- Los **objetos del modelo** transportan datos entre capas. No contienen lógica.

Los enlaces internos siempre usan `${pageContext.request.contextPath}` y apuntan al
controlador, nunca directamente a un archivo `.jsp`. Las vistas se invocan a través
del controlador.

### Configuración de la persistencia

Los datos de conexión no están escritos dentro del `persistence.xml`. La clase
`JPAUtil` lee `db.properties` y se los entrega a JPA al construir la fábrica de
`EntityManager`. Así las credenciales se mantienen fuera del control de versiones,
igual que en la Fase 1.

La unidad de persistencia usa `RESOURCE_LOCAL` porque Tomcat no administra
transacciones, y `hibernate.hbm2ddl.auto=validate`, que verifica que las entidades
coincidan con las tablas sin crear ni modificar nada. El esquema lo mantiene el
script de la base.

---

## Funcionalidad implementada

**Pacientes** — listar, crear, editar, eliminar.
**Médicos** — listar, crear, editar, eliminar, con selección de especialidad desde
lista desplegable.

**Validaciones**
- Cliente: atributos `required` en los formularios.
- Servidor: verificación de campos obligatorios antes de llamar a la capa de datos.
- Base de datos: restricción `UNIQUE` en el DUI, con mensaje claro al usuario cuando
  se intenta duplicar.

**Manejo de errores** — las excepciones `SQLException` se capturan en el controlador y
se muestran en la vista `views/mensajes/error.jsp`.

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

---

## Solución de problemas frecuentes

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `Access denied for user 'root'@'localhost'` | El MySQL de XAMPP ocupa el puerto 3306 | Ver la sección siguiente |
| `Failed to delete ...\target\...` | El proyecto está en OneDrive | Mover a `C:\proyectos` |
| `Unable to load the mojo 'war'` | `maven-war-plugin` obsoleto | Ya corregido en el `pom.xml` (versión 3.4.0) |
| `package javax.ws.rs does not exist` | Archivos de plantilla JAX-RS | Eliminar `JakartaRestConfiguration.java` y la carpeta `resources/` del paquete |
| `unreported exception java.sql.SQLException` | El Servlet llama al DAO sin `try/catch` | Envolver la llamada en `try/catch` |
| Nombres con `?` o caracteres raros | Script importado sin `--default-character-set=utf8mb4` | Reimportar con el comando de la sección 3 |
| `Archivo JSP no encontrado` | Ruta del controlador no coincide con el nombre del archivo | Verificar que `getRequestDispatcher` apunte al nombre real |
| `Public Key Retrieval is not allowed` | Configuración de MySQL 8.4 | Ya incluido en la URL de `db.properties.example` |
| 404 al abrir la aplicación | No está desplegada | Ejecutar con `F6` desde NetBeans |
| `Unable to create requested service [JdbcEnvironment]` | Hibernate no logró conectar | El error real está al final del seguimiento, normalmente es la contraseña o el puerto |

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

- En el modelo `Medico`, el atributo se llama `jvpm` pero la columna de la base es `dui`.
  Se unifica al convertir la clase en entidad JPA.
- El `doGet` de `PacienteServlet` captura las excepciones e imprime en consola sin
  informar al usuario. El `doPost` sí las maneja correctamente.
- `PruebaConexionServlet` y `PruebaJPAServlet` son herramientas de diagnóstico y se
  eliminan al cerrar la Fase 2.
- Los formularios usan `formulario.jsp` tanto para crear como para editar; las vistas
  `editar.jsp` quedaron sin uso.
- La contraseña de base de datos usada en desarrollo es débil y debe robustecerse antes
  del despliegue de la Fase 4.

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

**Fase 2 — Persistencia Empresarial e Integración JSF** · En desarrollo
Entrega: 10 u 11 de octubre de 2026 · 25% de la nota

- [x] Configuración de Hibernate y la unidad de persistencia
- [x] Lectura de credenciales externas al arrancar JPA
- [ ] Entidades JPA con sus relaciones
- [ ] Operaciones CRUD con JPA y transacciones
- [ ] Tabla de citas y datos de prueba
- [ ] Managed Beans
- [ ] Vistas JSF sobre AdminLTE
- [ ] Reglas de negocio del agendamiento
- [ ] Validadores, convertidores y operaciones AJAX
- [ ] PDF de evidencias

### Alcance

La Fase 1 cubrió los módulos de **pacientes** y **médicos** con JDBC.
La Fase 2 incorpora el módulo de **citas**, migra la persistencia a JPA e implementa
la interfaz con JSF sobre la plantilla AdminLTE, solicitada durante la defensa anterior.
