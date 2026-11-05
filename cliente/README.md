# Cliente consumidor de la API

Aplicación independiente en React + Vite para consultar, agendar, editar y cancelar citas. Se ejecuta como cliente de desarrollo en `http://localhost:5173`; el backend Java sigue ejecutándose en Tomcat.

## Requisitos

- Node.js 20 o superior y npm.
- API desplegada en `http://localhost:8080/clinica-citas`.

## Arranque

```powershell
cd cliente
Copy-Item .env.example .env
npm install
npm run dev
```

Abre `http://localhost:5173`. Para compilar el cliente: `npm run build`.

## Configuración

`VITE_API_BASE_URL` define la URL base, sin barra final. La aplicación usa por defecto `http://localhost:8080/clinica-citas/api`.

## Contrato REST esperado

El ZIP trae Jersey, los DTO y el mapeo `/api/*`, pero todavía no implementa recursos REST. Las rutas usadas por esta primera versión del cliente son una propuesta que deberá coincidir con los recursos que implemente el backend:

| Método | Ruta | Uso |
|---|---|---|
| GET | `/citas?idPaciente={id}` | Consultar citas; sin filtro, consultar listado general |
| POST | `/citas` | Agendar con `idPaciente`, `idMedico`, `fecha` (`dd/MM/yyyy`), `hora` (`HH:mm`) y `motivo` |
| PUT | `/citas/{id}` | Reprogramar fecha/hora o actualizar datos |
| PATCH | `/citas/{id}/cancelar` | Cancelar conservando el historial |
| GET | `/pacientes` | Cargar selección de paciente |
| GET | `/medicos` | Cargar selección de médico |

El listado puede ser un arreglo JSON o un objeto con `datos`/`citas`; pacientes y médicos pueden ser arreglos u objetos `datos`/`pacientes` y `datos`/`medicos`. Las respuestas de error esperadas tienen `mensaje`, como el DTO `ErrorRespuesta` existente.

El backend debe habilitar CORS para `http://localhost:5173` durante desarrollo y devolver JSON con los códigos HTTP adecuados. Antes de desplegarlo, debe autenticar y autorizar al paciente para que solo pueda consultar y administrar sus citas; el filtro actual permite todas las rutas `/api/*` sin sesión.

La disponibilidad de horarios y el inicio de sesión no se conectan todavía: no hay endpoints REST implementados para esas funciones en el repositorio recibido.
