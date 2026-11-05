package sv.edu.udb.clinica.api.recurso;

import static sv.edu.udb.clinica.api.util.Campos.limpiar;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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
import sv.edu.udb.clinica.api.dto.CitaRespuesta;
import sv.edu.udb.clinica.api.dto.CitaSolicitud;
import sv.edu.udb.clinica.api.dto.ErrorRespuesta;
import sv.edu.udb.clinica.api.error.ApiExcepcion;
import sv.edu.udb.clinica.api.util.LectorJson;
import sv.edu.udb.clinica.dao.CitaDAO;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Servicio REST de citas.
 *
 *   GET    /api/citas                      listado, con filtros opcionales:
 *                                          ?idPaciente= ?idMedico= ?fecha= ?estado=
 *   GET    /api/citas/{id}                 una cita
 *   GET    /api/citas/agenda               agenda del dia de un medico (HU-14):
 *                                          ?idMedico= &fecha=   (sin fecha, hoy)
 *   POST   /api/citas                      agendar              201 + Location
 *   PUT    /api/citas/{id}                 reprogramar/editar   200
 *   PATCH  /api/citas/{id}/cancelar        cancelar             200
 *   PATCH  /api/citas/{id}/atender         marcar atendida      200
 *
 * Errores: 400 datos invalidos, 404 no existe, 409 regla de negocio
 * (medico ocupado, fecha pasada, cita ya cerrada). Todos con el
 * formato de ErrorRespuesta.
 *
 * Las reglas son las mismas que aplica CitaBean en las pantallas, y el
 * acceso a datos es el mismo CitaDAO: la API no duplica la logica de
 * persistencia, solo la expone.
 */
@Path("citas")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class CitaRecurso {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.api");

    /** uuuu + STRICT rechaza fechas imposibles como 31/02/2026. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final CitaDAO citaDAO = new CitaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    @Context
    private UriInfo uriInfo;

    // ==================== Consultas ====================

    /**
     * Listado con filtros combinables. Todos son opcionales; sin
     * ninguno devuelve todas las citas.
     *
     * El filtrado se hace en memoria sobre la consulta que ya trae las
     * relaciones resueltas, para no multiplicar las consultas a la base
     * segun la combinacion de filtros que llegue.
     */
    @GET
    public List<CitaRespuesta> listar(@QueryParam("idPaciente") Integer idPaciente,
                                      @QueryParam("idMedico") Integer idMedico,
                                      @QueryParam("fecha") String fechaTexto,
                                      @QueryParam("estado") String estadoTexto) {

        final LocalDate fecha = (limpiar(fechaTexto) == null)
                ? null : interpretarFecha(fechaTexto, "fecha");
        final EstadoCita estado = (limpiar(estadoTexto) == null)
                ? null : interpretarEstado(estadoTexto);

        return citaDAO.listarCitas().stream()
                .filter(c -> idPaciente == null
                        || (c.getPaciente() != null
                        && Objects.equals(c.getPaciente().getIdPaciente(), idPaciente)))
                .filter(c -> idMedico == null
                        || (c.getMedico() != null
                        && Objects.equals(c.getMedico().getIdMedico(), idMedico)))
                .filter(c -> fecha == null || fecha.equals(c.getFecha()))
                .filter(c -> estado == null || estado.equals(c.getEstado()))
                .map(CitaRespuesta::desde)
                .collect(Collectors.toList());
    }

    /**
     * El {id: \\d+} hace que /api/citas/abc no llegue a este metodo
     * y Jersey responda 404, en lugar de un 500 al convertir "abc".
     */
    @GET
    @Path("{id: \\d+}")
    public CitaRespuesta obtener(@PathParam("id") int id) {
        return CitaRespuesta.desde(buscarOFallar(id));
    }

    /**
     * Agenda del dia de un medico (HU-14). Devuelve todas sus citas de
     * esa fecha, ordenadas por hora, cualquiera sea su estado: el
     * medico necesita saber tanto lo que va a atender como lo que se
     * cancelo.
     */
    @GET
    @Path("agenda")
    public List<CitaRespuesta> agendaDelDia(@QueryParam("idMedico") Integer idMedico,
                                            @QueryParam("fecha") String fechaTexto) {

        if (idMedico == null) {
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "Debe indicar el médico en el parámetro idMedico."));
        }
        if (medicoDAO.buscarPorId(idMedico) == null) {
            throw new ApiExcepcion(ErrorRespuesta.noEncontrado(
                    "No existe un médico con el número " + idMedico + "."));
        }

        LocalDate fecha = (limpiar(fechaTexto) == null)
                ? LocalDate.now()
                : interpretarFecha(fechaTexto, "fecha");

        return citaDAO.agendaDelDia(idMedico, fecha).stream()
                .map(CitaRespuesta::desde)
                .collect(Collectors.toList());
    }

    // ==================== Escritura ====================

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agendar(String cuerpo) {
        CitaSolicitud datos = LectorJson.leer(cuerpo, CitaSolicitud.class);

        Cita cita = new Cita();
        cita.setEstado(EstadoCita.PROGRAMADA);
        aplicar(datos, cita, null);

        citaDAO.insertar(cita);
        LOG.info("API: cita agendada, id " + cita.getIdCita());

        URI ubicacion = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(cita.getIdCita())).build();
        return Response.created(ubicacion)
                .entity(CitaRespuesta.desde(cita))
                .build();
    }

    /**
     * Reprograma o corrige una cita.
     *
     * Si cambia la fecha o la hora, la cita pasa a REPROGRAMADA: ese
     * estado existe justamente para distinguir una cita movida de una
     * agendada de primera, y asi queda registrado en el historial.
     *
     * Una cita cancelada o ya atendida no se edita: su horario ya se
     * libero o la consulta ya ocurrio, de modo que modificarla
     * falsearia el historial. Para esos casos se agenda una nueva.
     */
    @PUT
    @Path("{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CitaRespuesta actualizar(@PathParam("id") int id, String cuerpo) {
        Cita cita = buscarOFallar(id);

        if (!cita.getEstado().ocupaHorario()) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "No se puede modificar una cita " + cita.getEstado().getEtiqueta().toLowerCase()
                    + ". Agende una cita nueva."));
        }

        CitaSolicitud datos = LectorJson.leer(cuerpo, CitaSolicitud.class);

        LocalDate fechaAnterior = cita.getFecha();
        LocalTime horaAnterior = cita.getHora();

        aplicar(datos, cita, id);

        boolean seMovio = !Objects.equals(fechaAnterior, cita.getFecha())
                || !Objects.equals(horaAnterior, cita.getHora());
        if (seMovio) {
            cita.setEstado(EstadoCita.REPROGRAMADA);
        }

        citaDAO.actualizar(cita);
        LOG.info("API: cita actualizada, id " + id + (seMovio ? " (reprogramada)" : ""));
        return CitaRespuesta.desde(cita);
    }

    /**
     * Cancelar no elimina la cita: cambia su estado y libera el horario
     * del medico, conservando el historial.
     */
    @PATCH
    @Path("{id: \\d+}/cancelar")
    public CitaRespuesta cancelar(@PathParam("id") int id) {
        return cambiarEstado(id, EstadoCita.CANCELADA);
    }

    @PATCH
    @Path("{id: \\d+}/atender")
    public CitaRespuesta atender(@PathParam("id") int id) {
        return cambiarEstado(id, EstadoCita.ATENDIDA);
    }

    // ==================== Apoyo ====================

    private Cita buscarOFallar(int id) {
        Cita cita = citaDAO.buscarPorId(id);
        if (cita == null) {
            throw new ApiExcepcion(ErrorRespuesta.noEncontrado(
                    "No existe una cita con el número " + id + "."));
        }
        return cita;
    }

    private CitaRespuesta cambiarEstado(int id, EstadoCita nuevoEstado) {
        Cita cita = buscarOFallar(id);

        if (nuevoEstado.equals(cita.getEstado())) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "La cita ya está marcada como "
                    + nuevoEstado.getEtiqueta().toLowerCase() + "."));
        }
        if (!cita.getEstado().ocupaHorario()) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "La cita está " + cita.getEstado().getEtiqueta().toLowerCase()
                    + " y ya no admite cambios de estado."));
        }

        citaDAO.cambiarEstado(id, nuevoEstado);
        LOG.info("API: cita " + id + " marcada como " + nuevoEstado.name());
        return CitaRespuesta.desde(citaDAO.buscarPorId(id));
    }

    /**
     * Valida todos los campos de una vez y, si todo esta bien, los
     * copia a la entidad.
     *
     * Primero se revisa el formato de todos los campos y se devuelven
     * juntos, para que el formulario del cliente pueda marcarlos en un
     * solo intento. Solo despues se comprueban las reglas de negocio,
     * que responden 409 en lugar de 400: la peticion estaba bien
     * escrita, lo que no se puede es hacerla.
     *
     * idExcluir lleva el numero de la cita que se esta editando, para
     * que no choque consigo misma al revisar la agenda del medico. Al
     * agendar una nueva se pasa null.
     */
    private void aplicar(CitaSolicitud d, Cita cita, Integer idExcluir) {
        Map<String, String> errores = new LinkedHashMap<>();

        // ----- Formato y presencia -----

        Paciente paciente = null;
        if (d.getIdPaciente() == null) {
            errores.put("idPaciente", "Debe indicar el paciente.");
        } else {
            paciente = pacienteDAO.buscarPorId(d.getIdPaciente());
            if (paciente == null) {
                errores.put("idPaciente",
                        "No existe un paciente con el número " + d.getIdPaciente() + ".");
            }
        }

        Medico medico = null;
        if (d.getIdMedico() == null) {
            errores.put("idMedico", "Debe indicar el médico.");
        } else {
            medico = medicoDAO.buscarPorId(d.getIdMedico());
            if (medico == null) {
                errores.put("idMedico",
                        "No existe un médico con el número " + d.getIdMedico() + ".");
            }
        }

        LocalDate fecha = null;
        String fechaTexto = limpiar(d.getFecha());
        if (fechaTexto == null) {
            errores.put("fecha", "La fecha es obligatoria.");
        } else {
            try {
                fecha = LocalDate.parse(fechaTexto, FORMATO_FECHA);
            } catch (DateTimeParseException ex) {
                errores.put("fecha",
                        "La fecha debe tener el formato dd/MM/yyyy y ser una fecha real.");
            }
        }

        LocalTime hora = null;
        String horaTexto = limpiar(d.getHora());
        if (horaTexto == null) {
            errores.put("hora", "La hora es obligatoria.");
        } else {
            hora = interpretarHora(horaTexto);
            if (hora == null) {
                errores.put("hora",
                        "La hora debe tener el formato HH:mm, por ejemplo 09:30.");
            }
        }

        String motivo = limpiar(d.getMotivo());
        if (motivo != null && motivo.length() > 255) {
            errores.put("motivo", "El motivo no puede superar 255 caracteres.");
        }

        if (!errores.isEmpty()) {
            throw new ApiExcepcion(ErrorRespuesta.validacion(errores));
        }

        // ----- Reglas de negocio -----

        // No se agenda en una fecha y hora que ya paso. Se comprueban
        // juntas y no solo la fecha: una cita para hoy a las ocho de la
        // manana, pedida a las tres de la tarde, tambien es pasado.
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "No se puede agendar una cita en una fecha u hora que ya pasó."));
        }

        // Regla HU-12: el medico no puede tener dos citas vigentes a la
        // misma hora. Una cita cancelada o atendida no cuenta, porque
        // su horario volvio a quedar libre.
        if (citaDAO.medicoOcupado(medico.getIdMedico(), fecha, hora, idExcluir)) {
            throw new ApiExcepcion(ErrorRespuesta.conflicto(
                    "El médico " + medico.getNombres() + " " + medico.getApellidos()
                    + " ya tiene una cita agendada el " + fecha.format(FORMATO_FECHA)
                    + " a las " + hora.format(FORMATO_HORA) + "."));
        }

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setMotivo(motivo);
    }

    private LocalDate interpretarFecha(String texto, String campo) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "El parámetro '" + campo + "' debe tener el formato dd/MM/yyyy."));
        }
    }

    /** Acepta tanto 9:30 como 09:30. */
    private LocalTime interpretarHora(String texto) {
        try {
            return LocalTime.parse(texto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException ex) {
            try {
                return LocalTime.parse(texto.trim(), DateTimeFormatter.ofPattern("H:mm"));
            } catch (DateTimeParseException ex2) {
                return null;
            }
        }
    }

    private EstadoCita interpretarEstado(String texto) {
        try {
            return EstadoCita.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "Estado desconocido. Valores válidos: PROGRAMADA, "
                    + "REPROGRAMADA, ATENDIDA, CANCELADA."));
        }
    }
}
