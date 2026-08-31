# Sistema de Gestión de Citas — Clínica

Proyecto de cátedra de **DWF901 — Desarrollo de Aplicaciones con Web Frameworks**
Universidad Don Bosco, ciclo II 2026.

Aplicación Java Web con arquitectura MVC para administrar pacientes, médicos y citas
de una clínica.

---

## Integrantes

| # | Nombre | Rol en Fase 1 |
|---|--------|---------------|
| 1 | Dani | Vistas JSP / JSTL |
| 2 | Levi | Servlets / controladores |
| 3 | Marylin | DAOs y acceso a datos |
| 4 | Navarro | Base de datos y script SQL |
| 5 | Fernando | Arquitectura, POJOs, Git y documentación |

---

## Requisitos

| Herramienta | Versión | Nota |
|-------------|---------|------|
| JDK | 21 (LTS) | Eclipse Adoptium (Temurin) |
| Apache NetBeans | 29 | Cualquier IDE con soporte Maven funciona |
| Apache Tomcat | **9.0.121** | Usa el paquete `javax.servlet` |
| MySQL Server | 8.4.11 (LTS) | |
| Maven | Incluido en NetBeans | |

> **Importante:** el proyecto usa `javax.servlet`, no `jakarta.servlet`.
> Tomcat 10 o superior **no** ejecutará esta aplicación.

---

## Antes de empezar

**No coloques el proyecto dentro de OneDrive, Google Drive ni Dropbox.**
La sincronización bloquea archivos mientras Maven compila y produce errores como
`Failed to delete ...\target\...` que parecen problemas de código pero no lo son.

Ubicación recomendada: `C:\proyectos\clinica-citas`

Lo mismo aplica a Tomcat: instalarlo en una ruta sin espacios ni tildes,
por ejemplo `C:\tomcat9`.

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/Fercho0409/clinica-citas-dwf901.git
cd clinica-citas-dwf901
```

### 2. Crear la base de datos

Desde el cliente de MySQL:

```sql
CREATE DATABASE clinica_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

El `utf8mb4` es necesario para que tildes y ñ se guarden correctamente.

Luego ejecutar el script de creación de tablas:

```bash
mysql -u root -p clinica_db < database/clinica_db.sql
```

### 3. Configurar las credenciales

Copiar el archivo de ejemplo y editarlo con los datos locales:

```bash
copy src\main\resources\db.properties.example src\main\resources\db.properties
```

Editar `db.properties` y colocar la contraseña de MySQL.

> `db.properties` está en `.gitignore` y **no debe subirse al repositorio**.
> Antes de cada `git push`, verificar con `git status` que no aparezca en la lista.

### 4. Configurar el IDE

1. Registrar el JDK 21 en **Tools → Java Platforms → Add Platform**
2. Registrar Tomcat 9 en la pestaña **Services** (usuario y contraseña: `admin` / `admin`)
3. En **Properties → Build → Compile**, seleccionar **JDK 21** como Java Platform

### 5. Ejecutar

Clic derecho en el proyecto → **Clean and Build**, luego `F6`.

La aplicación queda disponible en:
`http://localhost:8080/clinica-citas/`

Para verificar la conexión a la base de datos:
`http://localhost:8080/clinica-citas/prueba-conexion`

---

## Estructura del proyecto

```
src/main/java/sv/edu/udb/clinica/
├── modelo/         POJOs (Paciente, Medico, Especialidad)
├── dao/            Acceso a datos con JDBC y PreparedStatement
├── controlador/    Servlets
└── util/           ConexionBD y utilidades

src/main/resources/
├── db.properties           Credenciales locales (NO versionado)
└── db.properties.example   Plantilla de configuración

src/main/webapp/    Vistas JSP y recursos estáticos
database/           Script SQL de creación
```

La separación de capas es estricta:

- Las **JSP** solo muestran información. No acceden a datos.
- Los **Servlets** reciben peticiones, validan y coordinan. No escriben SQL.
- Los **DAO** son los únicos que ejecutan consultas, siempre con `PreparedStatement`.
- Los **POJO** transportan datos entre capas. No contienen lógica.

---

## Convenciones de trabajo

- La rama `main` se mantiene siempre estable. Nadie sube directo a ella.
- Cada tarea se desarrolla en su propia rama: `feature/dao-paciente`, `feature/vista-medico`, etc.
- Los cambios entran a `main` mediante Pull Request.
- Commits descriptivos en español, en imperativo:
  `Agregar validación de DUI en PacienteServlet`
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
| `package javax.ws.rs does not exist` | Archivos sobrantes de la plantilla | Borrar `JakartaRestConfiguration.java` y la carpeta `resources/` de `sv/edu/udb/clinica` |
| `Unable to load the mojo 'war'` | `maven-war-plugin` obsoleto | Ya corregido en el `pom.xml` (versión 3.4.0) |
| `Public Key Retrieval is not allowed` | Configuración de MySQL 8.4 | Ya incluido en la URL de `db.properties.example` |
| 404 al abrir la aplicación | No está desplegada | Ejecutar con `F6` desde NetBeans |

---

## Estado actual

**Fase 1 — Fundamentos Java Web y Arquitectura MVC**
Entrega: 12 o 13 de septiembre de 2026 · 20% de la nota

- [x] Configuración del proyecto y repositorio
- [x] Conexión a MySQL con configuración externa
- [x] POJOs del modelo
- [ ] Script de base de datos
- [ ] DAOs de paciente y médico
- [ ] Servlets de paciente y médico
- [ ] Vistas JSP con JSTL
- [ ] Validaciones de cliente y servidor
- [ ] PDF de evidencias y Product Backlog

### Alcance de esta fase

Solo los módulos de **pacientes** y **médicos** con sus operaciones CRUD.
El módulo de **citas** se implementa en la Fase 2, cuando se incorpore JPA.
