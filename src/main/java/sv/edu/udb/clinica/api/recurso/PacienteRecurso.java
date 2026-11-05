package sv.edu.udb.clinica.api.recurso;

import static sv.edu.udb.clinica.api.util.Campos.limpiar;
import static sv.edu.udb.clinica.api.util.Campos.obligatorio;
import static sv.edu.udb.clinica.api.util.Campos.anotar;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import sv.edu.udb.clinica.api.dto.ErrorRespuesta;
import sv.edu.udb.clinica.api.dto.PacienteRespuesta;
import sv.edu.udb.clinica.api.dto.PacienteResumen;
import sv.edu.udb.clinica.api.dto.PacienteSolicitud;
import sv.edu.udb.clinica.api.error.ApiExcepcion;
import sv.edu.udb.clinica.api.util.LectorJson;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.validator.ReglasValidacion;

/**
 * Servicio REST de pacientes.
 *
 *   GET    /api/pacientes             listado (resumen), ?buscar=texto
 *   GET    /api/pacientes/{id}        ficha completa
 *   POST   /api/pacientes             registrar            201 + Location
 *   PUT    /api/pacientes/{id}        actualizar           200
 *   DELETE /api/pacientes/{id}        eliminar             204
 *
 * Errores: 400 datos invalidos, 404 no existe, 409 DUI repetido o
 * paciente con citas. Todos con el formato de ErrorRespuesta.
 *
 * Las reglas son las mismas que aplica PacienteBean en las pantallas,
 * y el acceso a datos es el mismo PacienteDAO: la API no duplica la
 * logica, solo la expone.
 */
@Path("pacientes")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class PacienteRecurso {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.api");

    /** uuuu + STRICT rechaza fechas imposibles como 31/02/2000. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    @Context
    private UriInfo uriInfo;

    // ==================== Consultas ====================

    @GET
    public List<PacienteResumen> listar(@QueryParam("buscar") String buscar) {
        List<Paciente> pacientes = (buscar == null || buscar.trim().isEmpty())
                ? pacienteDAO.listarPacientes()
                : pacienteDAO.buscar(buscar);

        return pacientes.stream()
                .map(PacienteResumen::desde)
                .collect(Collectors.toList());
    }

    /**
     * El {id: \\d+} hace que /api/pacientes/abc no llegue a este metodo
     * y Jersey responda 404, en lugar de un 500 al convertir "abc".
     */
    @GET
    @Path("{id: \\d+}")
    public PacienteRespuesta obtener(@PathParam("id") int id) {
        return PacienteRespuesta.desde(buscarOFallar(id));
    }

    // ==================== Escritura ====================

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(String cuerpo) {
        PacienteSolicitud datos = LectorJson.leer(cuerpo, PacienteSolicitud.class);

        Paciente paciente = new Paciente();
        aplicar(datos, paciente);

        if (pacienteDAO.existeDui(paciente.getDui(), null)) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "Ya existe un paciente registrado con el DUI " + paciente.getDui() + "."));
        }

        pacienteDAO.insertar(paciente);
        LOG.info("API: paciente registrado, id " + paciente.getIdPaciente());

        URI ubicacion = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(paciente.getIdPaciente())).build();
        return Response.created(ubicacion)
                .entity(PacienteRespuesta.desde(paciente))
                .build();
    }

    @PUT
    @Path("{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public PacienteRespuesta actualizar(@PathParam("id") int id, String cuerpo) {
        Paciente paciente = buscarOFallar(id);
        PacienteSolicitud datos = LectorJson.leer(cuerpo, PacienteSolicitud.class);

        aplicar(datos, paciente);

        if (pacienteDAO.existeDui(paciente.getDui(), id)) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "Ya existe otro paciente registrado con el DUI " + paciente.getDui() + "."));
        }

        pacienteDAO.actualizar(paciente);
        LOG.info("API: paciente actualizado, id " + id);
        return PacienteRespuesta.desde(paciente);
    }

    @DELETE
    @Path("{id: \\d+}")
    public Response eliminar(@PathParam("id") int id) {
        buscarOFallar(id);

        long citas = pacienteDAO.contarCitas(id);
        if (citas > 0) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "No se puede eliminar el paciente porque tiene " + citas
                    + (citas == 1 ? " cita registrada." : " citas registradas.")
                    + " Las citas conservan el historial clínico."));
        }

        pacienteDAO.eliminar(id);
        LOG.info("API: paciente eliminado, id " + id);
        return Response.noContent().build();
    }

    // ==================== Apoyo ====================

    private Paciente buscarOFallar(int id) {
        Paciente paciente = pacienteDAO.buscarPorId(id);
        if (paciente == null) {
            throw new ApiExcepcion(ErrorRespuesta.noEncontrado(
                    "No existe un paciente con el número " + id + "."));
        }
        return paciente;
    }

    /**
     * Valida todos los campos de una vez y, si todo esta bien, los
     * copia a la entidad.
     *
     * Se revisan todos antes de responder para que el cliente pueda
     * marcar cada campo del formulario en un solo intento, en lugar de
     * descubrir los errores de uno en uno.
     */
    private void aplicar(PacienteSolicitud d, Paciente p) {
        Map<String, String> errores = new LinkedHashMap<>();

        String nombres = limpiar(d.getNombres());
        String apellidos = limpiar(d.getApellidos());
        String dui = limpiar(d.getDui());
        String telefono = limpiar(d.getTelefono());
        String correo = limpiar(d.getCorreo());
        String direccion = limpiar(d.getDireccion());

        obligatorio(errores, "nombres", nombres, 100);
        obligatorio(errores, "apellidos", apellidos, 100);
        anotar(errores, "dui", ReglasValidacion.validarDui(dui));
        anotar(errores, "telefono", ReglasValidacion.validarTelefono(telefono));
        anotar(errores, "correo", ReglasValidacion.validarCorreo(correo));
        if (correo != null && correo.length() > 100) {
            errores.putIfAbsent("correo", "El correo no puede superar 100 caracteres.");
        }
        if (direccion != null && direccion.length() > 200) {
            errores.put("direccion", "La dirección no puede superar 200 caracteres.");
        }

        LocalDate nacimiento = null;
        String fechaTexto = limpiar(d.getFechaNacimiento());
        if (fechaTexto == null) {
            errores.put("fechaNacimiento", "La fecha de nacimiento es obligatoria.");
        } else {
            try {
                nacimiento = LocalDate.parse(fechaTexto, FORMATO_FECHA);
                anotar(errores, "fechaNacimiento",
                        ReglasValidacion.validarFechaNacimiento(nacimiento));
            } catch (DateTimeParseException ex) {
                errores.put("fechaNacimiento",
                        "La fecha debe tener el formato dd/MM/yyyy y ser una fecha real.");
            }
        }

        if (!errores.isEmpty()) {
            throw new ApiExcepcion(ErrorRespuesta.validacion(errores));
        }

        p.setNombres(nombres);
        p.setApellidos(apellidos);
        p.setDui(dui);
        p.setFechaNacimiento(nacimiento);
        p.setTelefono(telefono);
        p.setCorreo(correo != null ? correo.toLowerCase() : null);
        p.setDireccion(direccion);
    }
}
