# API REST — Pacientes, médicos y especialidades

**Fase 3 · Sprint III** · Responsable: Integrante 3

URL base (NetBeans + Tomcat 9): `http://localhost:8080/clinica-citas/api`

Todas las respuestas son JSON (`application/json; charset=UTF-8`). Los cuerpos de
`POST` y `PUT` deben enviarse con `Content-Type: application/json`.

---

## Resumen de endpoints

| Método | Ruta | Descripción | Éxito | Errores |
|--------|------|-------------|-------|---------|
| GET | `/pacientes` | Listado resumido. `?buscar=texto` | 200 | — |
| GET | `/pacientes/{id}` | Ficha completa | 200 | 404 |
| POST | `/pacientes` | Registrar | 201 + `Location` | 400, 409, 415 |
| PUT | `/pacientes/{id}` | Actualizar | 200 | 400, 404, 409, 415 |
| DELETE | `/pacientes/{id}` | Eliminar | 204 | 404, 409 |
| GET | `/medicos` | Directorio. `?buscar=texto&especialidad=id` | 200 | 400 |
| GET | `/medicos/{id}` | Detalle | 200 | 404 |
| POST | `/medicos` | Registrar | 201 + `Location` | 400, 409, 415 |
| PUT | `/medicos/{id}` | Actualizar | 200 | 400, 404, 409, 415 |
| DELETE | `/medicos/{id}` | Eliminar | 204 | 404, 409 |
| GET | `/especialidades` | Catálogo | 200 | — |

### Significado de los códigos

| Código | Cuándo |
|--------|--------|
| 200 OK | Consulta o actualización correcta |
| 201 Created | Registro creado. El encabezado `Location` trae la URL del nuevo recurso |
| 204 No Content | Eliminado. Sin cuerpo |
| 400 Bad Request | JSON mal escrito, campo obligatorio vacío, formato inválido |
| 404 Not Found | El registro o la ruta no existen |
| 405 Method Not Allowed | Método no admitido en esa ruta (p. ej. `PATCH`) |
| 409 Conflict | Datos correctos que violan una regla: DUI repetido, registro con citas |
| 415 Unsupported Media Type | El cuerpo no se envió como JSON |
| 500 Internal Server Error | Fallo del servidor. El detalle queda en el registro de Tomcat, no en la respuesta |

---

## Formato de error

Todos los errores, incluidos los que genera Jersey (404 de ruta, 405, 415), tienen la
misma forma:

```json
{
  "codigo": 409,
  "error": "Conflicto",
  "mensaje": "Ya existe un paciente registrado con el DUI 04567890-3."
}
```

Cuando falla la validación de un formulario se agrega `campos`, con un mensaje por cada
campo incorrecto. Se devuelven **todos** los errores a la vez, para que el cliente marque
el formulario completo en un solo intento:

```json
{
  "codigo": 400,
  "error": "Solicitud invalida",
  "mensaje": "Hay 3 campos con errores.",
  "campos": {
    "nombres": "El campo nombres es obligatorio.",
    "dui": "El número de DUI ingresado no es válido (dígito verificador incorrecto).",
    "fechaNacimiento": "La fecha debe tener el formato dd/MM/yyyy y ser una fecha real."
  }
}
```

`mensaje` y cada valor de `campos` pueden mostrarse tal cual al usuario.

---

## Pacientes

### GET `/pacientes`

Devuelve un **resumen** de cada paciente. Parámetro opcional `buscar`: coincidencia
parcial en nombres, apellidos o DUI, sin distinguir mayúsculas.

```json
[
  {
    "idPaciente": 1,
    "nombres": "Carlos",
    "apellidos": "Martínez",
    "nombreCompleto": "Carlos Martínez",
    "duiParcial": "*****567-8",
    "edad": 31
  }
]
```

### GET `/pacientes/{id}`

Ficha completa, la que usa el formulario de edición.

```json
{
  "idPaciente": 1,
  "nombres": "Carlos",
  "apellidos": "Martínez",
  "nombreCompleto": "Carlos Martínez",
  "dui": "01234567-8",
  "fechaNacimiento": "15/03/1995",
  "edad": 31,
  "telefono": "7012-3456",
  "correo": "carlos.martinez@gmail.com",
  "direccion": "San Salvador"
}
```

### POST `/pacientes` · PUT `/pacientes/{id}`

```json
{
  "nombres": "Andrea",
  "apellidos": "Rivas Molina",
  "dui": "04567890-3",
  "fechaNacimiento": "14/02/2000",
  "telefono": "7890-1234",
  "correo": "andrea.rivas@gmail.com",
  "direccion": "Colonia Escalón, San Salvador"
}
```

`PUT` reemplaza todos los campos editables: se envía el paciente completo, igual que en el
`POST`. Si el cuerpo trae `idPaciente` se ignora; el número va en la ruta.

| Campo | Regla |
|-------|-------|
| `nombres`, `apellidos` | Obligatorios, máximo 100 caracteres |
| `dui` | Obligatorio, formato `00000000-0`, dígito verificador válido, único |
| `fechaNacimiento` | Obligatoria, `dd/MM/yyyy`, fecha real (rechaza `31/02/2000`), no futura, no más de 120 años |
| `telefono` | Opcional, `0000-0000`, inicia en 2, 6, 7 u 8 |
| `correo` | Opcional, formato válido, máximo 100. Se guarda en minúsculas |
| `direccion` | Opcional, máximo 200 caracteres |

Los espacios al inicio y al final se recortan; un campo opcional vacío se guarda como nulo.

### DELETE `/pacientes/{id}`

Devuelve `409` si el paciente tiene citas registradas, en cualquier estado: las citas son
historial clínico y la llave foránea de la base impide borrarlas en cascada.

---

## Médicos

### GET `/medicos`

Filtros opcionales: `buscar` (nombres o apellidos) y `especialidad` (número). Si
`especialidad` no es un número se responde `400`.

```json
[
  {
    "idMedico": 2,
    "nombres": "Patricia",
    "apellidos": "Gómez",
    "nombreCompleto": "Patricia Gómez",
    "idEspecialidad": 2,
    "especialidad": "Pediatría",
    "telefono": "7022-3344",
    "correo": "patricia.gomez@clinica.com"
  }
]
```

### POST `/medicos` · PUT `/medicos/{id}`

```json
{
  "nombres": "Mario",
  "apellidos": "Fuentes",
  "dui": "05123456-3",
  "idEspecialidad": 2,
  "telefono": "2250-1100",
  "correo": "mario.fuentes@clinica.com"
}
```

| Campo | Regla |
|-------|-------|
| `nombres`, `apellidos` | Obligatorios, máximo 100 caracteres |
| `dui` | **POST:** obligatorio, mismas reglas que en pacientes. **PUT:** opcional; si no se envía, se conserva el guardado |
| `idEspecialidad` | Obligatorio; debe existir en `GET /especialidades` |
| `telefono`, `correo` | Opcionales, mismas reglas que en pacientes |

### DELETE `/medicos/{id}`

`409` si el médico tiene citas registradas.

---

## Especialidades

### GET `/especialidades`

```json
[
  { "idEspecialidad": 3, "nombre": "Cardiología",
    "descripcion": "Diagnóstico y tratamiento de enfermedades cardiovasculares" }
]
```

---

## Qué datos se exponen y por qué

La API nunca convierte una entidad JPA directamente a JSON. Cada respuesta tiene su propia
clase en `api/dto`, de modo que lo que sale hacia afuera es una decisión explícita y no un
efecto de cómo está armada la tabla.

| Dato | Dónde aparece | Motivo |
|------|---------------|--------|
| Contraseña de usuario (`usuarios.clave`) | **Nunca** | No existe ningún endpoint de usuarios. Ningún DTO tiene referencia a `Usuario` |
| DUI del paciente | Solo en la ficha (`/pacientes/{id}`). En el listado sale enmascarado: `*****567-8` | El listado entrega los datos de todos a la vez. Los últimos dígitos bastan para distinguir a dos personas con el mismo nombre |
| Teléfono, correo y dirección del paciente | Solo en la ficha | Datos de contacto personales; no son necesarios para elegir a un paciente en una lista |
| DUI del médico | **Nunca** | El directorio de médicos es para sistemas externos y el portal del paciente (HU-17). Ninguno lo necesita: al médico se le identifica por `idMedico` |
| Teléfono y correo del médico | Sí | Contacto profesional en la clínica |

---

## Manejo de errores y registro

- `ManejadorErrores` (`api/error`) es el único `ExceptionMapper` de la API. Convierte
  cualquier excepción en una `ErrorRespuesta`: las previstas (`ApiExcepcion`) con su
  código, las de Jersey (404, 405, 415) con mensaje en español, y cualquier otra en un 500
  genérico. El rastro de la excepción se escribe en el registro de Tomcat y nunca se envía
  al cliente.
- Los cuerpos JSON se leen con `LectorJson` (Jackson) dentro del recurso y no de forma
  automática, para responder a un JSON mal escrito con un mensaje claro y no con el
  mensaje interno del convertidor.
- Altas, cambios, bajas y errores se registran con `java.util.logging` en la categoría
  `sv.edu.udb.clinica.api` (salida de Tomcat en NetBeans).

## CORS

`FiltroCors` agrega los encabezados que permiten al cliente consumidor llamar a la API
desde otro puerto (por ejemplo `http://localhost:5173`). En desarrollo se acepta cualquier
origen; en la Fase 4 debe restringirse al dominio donde se despliegue el cliente.

## Validaciones compartidas con las pantallas

Las reglas de DUI, teléfono y correo se movieron de los validadores JSF a
`validator/ReglasValidacion`, una clase sin dependencias de JSF. Los validadores
`DuiValidator`, `EmailValidator` y `TelefonoValidator` ahora delegan en ella, de modo que
las pantallas y la API aplican exactamente la misma regla.

---

## Pruebas

Colección Postman: `docs/postman/clinica-api-pacientes-medicos.postman_collection.json`

35 peticiones con verificaciones automáticas (casos positivos, negativos y límites).
Importarla en Postman y ejecutarla completa con **Run collection**, en orden, sobre una
base recién cargada con `clinicafinal.sql`: las peticiones guardan los `id` creados en
variables y los casos `P17` y `M12` dependen de que el paciente 1 y el médico 1 del script
tengan citas.

DUI válidos para pruebas manuales: `04567890-3`, `05123456-3`, `03987654-3`.
