package sv.edu.udb.clinica.api.recurso;

import static sv.edu.udb.clinica.api.util.Campos.limpiar;
import static sv.edu.udb.clinica.api.util.Campos.obligatorio;
import static sv.edu.udb.clinica.api.util.Campos.anotar;

import java.net.URI;
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
import sv.edu.udb.clinica.api.dto.MedicoRespuesta;
import sv.edu.udb.clinica.api.dto.MedicoSolicitud;
import sv.edu.udb.clinica.api.error.ApiExcepcion;
import sv.edu.udb.clinica.api.util.LectorJson;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.validator.ReglasValidacion;

/**
 * Servicio REST de medicos.
 *
 *   GET    /api/medicos               listado, ?buscar=texto&especialidad=id
 *   GET    /api/medicos/{id}          detalle
 *   POST   /api/medicos               registrar            201 + Location
 *   PUT    /api/medicos/{id}          actualizar           200
 *   DELETE /api/medicos/{id}          eliminar             204
 *
 * Errores: 400 datos invalidos o especialidad inexistente, 404 no
 * existe, 409 DUI repetido o medico con citas.
 *
 * Ninguna respuesta incluye el DUI del medico (ver MedicoRespuesta).
 */
@Path("medicos")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class MedicoRecurso {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.api");

    private final MedicoDAO medicoDAO = new MedicoDAO();

    @Context
    private UriInfo uriInfo;

    // ==================== Consultas ====================

    /**
     * especialidad se recibe como texto y se convierte aqui: si se
     * declara Integer y llega "abc", Jersey responde 404, que es
     * enganoso. Asi se responde 400 con la explicacion.
     */
    @GET
    public List<MedicoRespuesta> listar(@QueryParam("buscar") String buscar,
                                        @QueryParam("especialidad") String especialidad) {
        Integer idEspecialidad = null;
        String filtroEsp = limpiar(especialidad);
        if (filtroEsp != null) {
            try {
                idEspecialidad = Integer.valueOf(filtroEsp);
            } catch (NumberFormatException ex) {
                throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                        "El parámetro 'especialidad' debe ser el número de una especialidad."));
            }
        }

        return medicoDAO.buscar(limpiar(buscar), idEspecialidad).stream()
                .map(MedicoRespuesta::desde)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id: \\d+}")
    public MedicoRespuesta obtener(@PathParam("id") int id) {
        return MedicoRespuesta.desde(buscarOFallar(id));
    }

    // ==================== Escritura ====================

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(String cuerpo) {
        MedicoSolicitud datos = LectorJson.leer(cuerpo, MedicoSolicitud.class);

        Medico medico = new Medico();
        aplicar(datos, medico, true);

        if (medicoDAO.existeDui(medico.getDui(), null)) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "Ya existe un médico registrado con ese DUI."));
        }

        medicoDAO.insertar(medico);
        LOG.info("API: medico registrado, id " + medico.getIdMedico());

        URI ubicacion = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(medico.getIdMedico())).build();
        return Response.created(ubicacion)
                .entity(MedicoRespuesta.desde(medico))
                .build();
    }

    @PUT
    @Path("{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public MedicoRespuesta actualizar(@PathParam("id") int id, String cuerpo) {
        Medico medico = buscarOFallar(id);
        MedicoSolicitud datos = LectorJson.leer(cuerpo, MedicoSolicitud.class);

        boolean cambiaDui = limpiar(datos.getDui()) != null;
        aplicar(datos, medico, cambiaDui);

        if (cambiaDui && medicoDAO.existeDui(medico.getDui(), id)) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "Ya existe otro médico registrado con ese DUI."));
        }

        medicoDAO.actualizar(medico);
        LOG.info("API: medico actualizado, id " + id);
        return MedicoRespuesta.desde(medico);
    }

    @DELETE
    @Path("{id: \\d+}")
    public Response eliminar(@PathParam("id") int id) {
        buscarOFallar(id);

        long citas = medicoDAO.contarCitas(id);
        if (citas > 0) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "No se puede eliminar el médico porque tiene " + citas
                    + (citas == 1 ? " cita registrada." : " citas registradas.")));
        }

        medicoDAO.eliminar(id);
        LOG.info("API: medico eliminado, id " + id);
        return Response.noContent().build();
    }

    // ==================== Apoyo ====================

    private Medico buscarOFallar(int id) {
        Medico medico = medicoDAO.buscarPorId(id);
        if (medico == null) {
            throw new ApiExcepcion(ErrorRespuesta.noEncontrado(
                    "No existe un médico con el número " + id + "."));
        }
        return medico;
    }

    /**
     * Valida todos los campos y los copia a la entidad.
     *
     * @param validarDui true al registrar, o al actualizar cuando el
     *                   cliente manda un DUI nuevo. Si es false se
     *                   conserva el DUI que ya tiene el medico.
     */
    private void aplicar(MedicoSolicitud d, Medico m, boolean validarDui) {
        Map<String, String> errores = new LinkedHashMap<>();

        String nombres = limpiar(d.getNombres());
        String apellidos = limpiar(d.getApellidos());
        String dui = limpiar(d.getDui());
        String telefono = limpiar(d.getTelefono());
        String correo = limpiar(d.getCorreo());

        obligatorio(errores, "nombres", nombres, 100);
        obligatorio(errores, "apellidos", apellidos, 100);
        if (validarDui) {
            anotar(errores, "dui", ReglasValidacion.validarDui(dui));
        }
        anotar(errores, "telefono", ReglasValidacion.validarTelefono(telefono));
        anotar(errores, "correo", ReglasValidacion.validarCorreo(correo));
        if (correo != null && correo.length() > 100) {
            errores.putIfAbsent("correo", "El correo no puede superar 100 caracteres.");
        }

        Especialidad especialidad = null;
        if (d.getIdEspecialidad() == null) {
            errores.put("idEspecialidad", "Debe indicar la especialidad.");
        } else {
            especialidad = medicoDAO.buscarEspecialidad(d.getIdEspecialidad());
            if (especialidad == null) {
                errores.put("idEspecialidad", "No existe una especialidad con el número "
                        + d.getIdEspecialidad() + ". Consulte GET /api/especialidades.");
            }
        }

        if (!errores.isEmpty()) {
            throw new ApiExcepcion(ErrorRespuesta.validacion(errores));
        }

        m.setNombres(nombres);
        m.setApellidos(apellidos);
        if (validarDui) {
            m.setDui(dui);
        }
        m.setTelefono(telefono);
        m.setCorreo(correo != null ? correo.toLowerCase() : null);
        m.setEspecialidad(especialidad);
    }
}
