# Sistema de Gestión de Citas — Clínica

Proyecto de cátedra de **DWF901 — Desarrollo de Aplicaciones con Web Frameworks**
Universidad Don Bosco · Ciclo II 2026

Aplicación Java Web con arquitectura MVC para administrar pacientes y médicos de una clínica.
Primer incremento (Fase 1) de un sistema que crecerá durante el ciclo hasta incluir citas,
servicios REST y seguridad por roles.

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

> **El proyecto usa `javax.servlet`, no `jakarta.servlet`.**
> Tomcat 10 o superior **no** ejecutará esta aplicación.

---

## Antes de empezar

**No coloques el proyecto dentro de OneDrive, Google Drive ni Dropbox.**
La sincronización bloquea archivos mientras Maven compila y produce errores como
`Failed to delete ...\target\...` que parecen problemas de código pero no lo son.

Ubicación recomendada: `C:\proyectos\clinica-citas`
Tomcat en una ruta sin espacios ni tildes: `C:\tomcat9`

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/Fercho0409/clinica-citas-dwf901.git
cd clinica-citas-dwf901
```

### 2. Crear y cargar la base de datos

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
aparecen como `Mart?nez` o `Mart├¡nez`. El archivo está bien; el problema es la
codificación de la consola de Windows.

Para verificar que quedó correcto:

```sql
SELECT HEX(nombres) FROM clinica.pacientes WHERE id_paciente = 3;
```

Debe devolver `4A6F73C3A9` (los bytes `C3A9` son la `é` en UTF-8).

### 3. Configurar las credenciales

```bash
copy src\main\resources\db.properties.example src\main\resources\db.properties
```

Editar `db.properties` con la contraseña local de MySQL.

> `db.properties` está en `.gitignore` y **no debe subirse al repositorio**.
> Antes de cada `git push`, verificar con `git status` que no aparezca.

### 4. Configurar el IDE

1. Registrar el JDK 21 en **Tools → Java Platforms → Add Platform**
2. Registrar Tomcat 9 en la pestaña **Services** (usuario y contraseña: `admin` / `admin`)
3. En **Properties → Build → Compile**, seleccionar **JDK 21** como Java Platform

### 5. Ejecutar

Clic derecho en el proyecto → **Clean and Build**, luego `F6`.

- Portada: `http://localhost:8080/clinica-citas/`
- Pacientes: `http://localhost:8080/clinica-citas/pacientes`
- Médicos: `http://localhost:8080/clinica-citas/medicos`

---

## Estructura del proyecto

```
src/main/java/sv/edu/udb/clinica/
├── modelo/         POJOs: Paciente, Medico, Especialidad
├── dao/            PacienteDAO, MedicoDAO — JDBC con PreparedStatement
├── controlador/    PacienteServlet, MedicoServlet
└── util/           ConexionBD

src/main/resources/
├── db.properties           Credenciales locales (NO versionado)
└── db.properties.example   Plantilla de configuración

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

- Las **JSP** solo muestran información. No acceden a datos.
- Los **Servlets** reciben peticiones, validan y coordinan. No escriben SQL.
- Los **DAO** son los únicos que ejecutan consultas, siempre con `PreparedStatement`.
- Los **POJO** transportan datos entre capas. No contienen lógica.

Los enlaces internos siempre usan `${pageContext.request.contextPath}` y apuntan al
Servlet, nunca directamente a un archivo `.jsp`. Las vistas se invocan a través del
controlador.

---

## Funcionalidad implementada

**Pacientes** — listar, crear, editar, eliminar.
**Médicos** — listar, crear, editar, eliminar, con selección de especialidad desde
lista desplegable.

**Validaciones**
- Cliente: atributos `required` en los formularios.
- Servidor: verificación de campos obligatorios antes de llamar al DAO.
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
| `Failed to delete ...\target\...` | El proyecto está en OneDrive | Mover a `C:\proyectos` |
| `Unable to load the mojo 'war'` | `maven-war-plugin` obsoleto | Ya corregido en el `pom.xml` (versión 3.4.0) |
| `package javax.ws.rs does not exist` | Archivos de plantilla JAX-RS | Eliminar `JakartaRestConfiguration.java` y la carpeta `resources/` del paquete |
| `unreported exception java.sql.SQLException` | El Servlet llama al DAO sin `try/catch` | Envolver la llamada en `try/catch` |
| Nombres con `?` o caracteres raros | Script importado sin `--default-character-set=utf8mb4` | Reimportar con el comando de la sección 2 |
| `Archivo JSP no encontrado` | Ruta del Servlet no coincide con el nombre del archivo | Verificar que `getRequestDispatcher` apunte al nombre real |
| `Public Key Retrieval is not allowed` | Configuración de MySQL 8.4 | Ya incluido en la URL de `db.properties.example` |
| 404 al abrir la aplicación | No está desplegada | Ejecutar con `F6` desde NetBeans |

---

## Deuda técnica

Pendientes conocidos, a resolver en la Fase 2:

- En el modelo `Medico`, el atributo se llama `jvpm` pero la columna de la base es `dui`.
  El DAO hace el mapeo y funciona, pero conviene unificar el nombre.
- El `doGet` de `PacienteServlet` captura las excepciones e imprime en consola sin
  informar al usuario. El `doPost` sí las maneja correctamente.
- `PruebaConexionServlet` fue una herramienta de diagnóstico inicial y ya no es
  necesaria; puede eliminarse.
- Los formularios usan `formulario.jsp` tanto para crear como para editar; las vistas
  `editar.jsp` quedaron sin uso.

---

## Estado

**Fase 1 — Fundamentos Java Web y Arquitectura MVC**
Entrega: septiembre de 2026 · 20% de la nota

- [x] Configuración del proyecto y repositorio
- [x] Base de datos MySQL y script de creación
- [x] POJOs del modelo
- [x] Conexión con configuración externa
- [x] DAOs con consultas parametrizadas
- [x] Servlets controladores
- [x] Vistas JSP con JSTL
- [x] CRUD completo de pacientes y médicos
- [x] Validaciones de cliente y servidor
- [ ] PDF de evidencias

### Alcance

Esta fase cubre los módulos de **pacientes** y **médicos**.
El módulo de **citas** corresponde a la Fase 2, cuando se incorpore JPA o Hibernate.
