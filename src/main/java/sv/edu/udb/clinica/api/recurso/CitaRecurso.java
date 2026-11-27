package sv.edu.udb.clinica.api.recurso;

import static sv.edu.udb.clinica.api.util.Campos.limpiar;

import java.net.URI;
import java.time.LocalDate;
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
import sv.edu.udb.clinica.config.Contexto;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.servicio.CitaServicio;

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
 * ===================================================================
 *  Que cambio en la Fase 4
 * ===================================================================
 *
 * Este archivo tenia escritas las reglas del agendamiento, las mismas
 * que estaban tambien en CitaBean para las pantallas. Era la deuda
 * tecnica DT-15, con el riesgo de que la clinica se comportara
 * distinto segun por donde entrara la peticion.
 *
 * Ahora las reglas viven en CitaServicio y aqui no queda ninguna. Lo
 * que si se conserva es la validacion de formato, y eso no es un
 * descuido: comprobar que la fecha venga escrita como dd/MM/yyyy es
 * una preocupacion de la API, no del negocio. Las pantallas no la
 * necesitan porque el calendario no deja escribir una fecha mal.
 *
 * El reparto quedo asi:
 *
 *   este archivo   revisa como viene escrito y junta los errores de
 *                  formato de todos los campos, para que el cliente
 *                  pueda corregirlos en un solo intento (400)
 *
 *   CitaServicio   decide si lo que se pide se puede hacer, y avisa
 *                  lanzando una excepcion del dominio
 *
 *   MapeadorServicio  convierte esa excepcion en 404 o 409
 *
 * Por eso aqui ya no hay bloques try/catch envolviendo cada llamada:
 * las excepciones del servicio suben solas y el mapeador las atiende.
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

    /**
     * La capa de servicio, administrada por Spring.
     *
     * Jersey no sabe inyectar componentes de Spring, de modo que se le
     * piden a traves de Contexto. Esta es la unica linea del recurso
     * que conoce ese detalle.
     */
    private final CitaServicio citaServicio = Contexto.obtener(CitaServicio.class);

    /**
     * Solo se usan para comprobar que el paciente y el medico existan
     * mientras se revisan los campos, y poder devolver ese error junto
     * a los demas en una sola respuesta. Siguen construyendose a mano
     * porque esos modulos no se han migrado a Spring.
     */
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

        return citaServicio.listar().stream()
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
        return CitaRespuesta.desde(citaServicio.obtener(id));
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

        LocalDate fecha = (limpiar(fechaTexto) == null)
                ? LocalDate.now()
                : interpretarFecha(fechaTexto, "fecha");

        return citaServicio.agendaDelDia(idMedico, fecha).stream()
                .map(CitaRespuesta::desde)
                .collect(Collectors.toList());
    }

    // ==================== Escritura ====================

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agendar(String cuerpo) {
        CitaSolicitud datos = LectorJson.leer(cuerpo, CitaSolicitud.class);

        Cita cita = new Cita();
        revisarCampos(datos, cita);

        citaServicio.guardar(cita, datos.getIdPaciente(), datos.getIdMedico());
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
     * agendada de primera, y asi queda registrado en el historial. De
     * eso se encarga el servicio.
     *
     * Una cita cancelada o ya atendida no se edita: su horario ya se
     * libero o la consulta ya ocurrio, de modo que modificarla
     * falsearia el historial. Esa comprobacion tambien es del
     * servicio, y llega aqui convertida en un 409.
     */
    @PUT
    @Path("{id: \\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public CitaRespuesta actualizar(@PathParam("id") int id, String cuerpo) {
        Cita cita = citaServicio.obtener(id);
        citaServicio.verificarAbierta(cita);

        CitaSolicitud datos = LectorJson.leer(cuerpo, CitaSolicitud.class);
        revisarCampos(datos, cita);

        boolean seMovio = citaServicio.guardar(cita, datos.getIdPaciente(),
                datos.getIdMedico());
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
        Cita cita = citaServicio.cancelar(id);
        LOG.info("API: cita " + id + " cancelada");
        return CitaRespuesta.desde(cita);
    }

    @PATCH
    @Path("{id: \\d+}/atender")
    public CitaRespuesta atender(@PathParam("id") int id) {
        Cita cita = citaServicio.atender(id);
        LOG.info("API: cita " + id + " marcada como atendida");
        return CitaRespuesta.desde(cita);
    }

    // ==================== Revision de los campos ====================

    /**
     * Revisa como vienen escritos los campos y, si todo esta bien, los
     * copia a la entidad.
     *
     * Aqui NO hay reglas de negocio: no se comprueba si el medico esta
     * ocupado ni si la fecha ya paso. Eso lo decide CitaServicio
     * cuando se le pide guardar.
     *
     * Lo que si se hace, y es propio de la API, es juntar todos los
     * errores de formato en un solo lugar antes de rechazar. Si se
     * devolviera el primero que aparece, el cliente tendria que
     * corregir de uno en uno y volver a enviar cada vez.
     */
    private void revisarCampos(CitaSolicitud d, Cita cita) {
        Map<String, String> errores = new LinkedHashMap<>();

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