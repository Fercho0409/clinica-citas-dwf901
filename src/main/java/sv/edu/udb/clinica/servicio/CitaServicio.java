package sv.edu.udb.clinica.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.clinica.dao.CitaDAO;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Reglas de negocio de las citas.
 *
 * ===================================================================
 *  Por que existe esta clase
 * ===================================================================
 *
 * Hasta la Fase 3 las reglas del agendamiento estaban escritas dos
 * veces. Una en CitaBean, para las pantallas, y otra en CitaRecurso,
 * para la API. Eran las mismas reglas con distinta redaccion:
 *
 *   - un medico no puede tener dos citas vigentes a la misma hora
 *   - no se agenda en una fecha u hora que ya paso
 *   - una cita cancelada o atendida no admite cambios
 *   - al mover una cita de fecha u hora, pasa a reprogramada
 *
 * Eso quedo anotado como deuda tecnica DT-15 en el Product Backlog,
 * con el riesgo evidente: cambiar una regla obligaba a acordarse de
 * tocar los dos lados, y olvidar uno significaba que la clinica se
 * comportara distinto segun por donde entrara la peticion.
 *
 * Esta clase es el unico lugar donde viven esas reglas. Las pantallas
 * y la API ya no deciden nada: preguntan.
 *
 * ===================================================================
 *  Las anotaciones
 * ===================================================================
 *
 * @Service
 *      Igual que @Repository, le dice a Spring que administre la
 *      clase. Se usa un nombre distinto porque indica otra cosa: aqui
 *      no se consulta la base, aqui se decide.
 *
 * @Transactional(readOnly = true) sobre la clase
 *      Todo metodo corre dentro de una transaccion. Las de solo
 *      lectura le avisan a la base que no va a escribir, lo que le
 *      permite trabajar mas liviano.
 *
 * @Transactional en un metodo
 *      Sobrescribe lo anterior para los que si escriben. Lo
 *      importante es que la transaccion ahora envuelve toda la
 *      operacion y no cada consulta por separado: si al guardar una
 *      cita algo falla a mitad de camino, no queda nada escrito a
 *      medias.
 */
@Service
@Transactional(readOnly = true)
public class CitaServicio {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.servicio");

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final CitaDAO citaDAO;

    /**
     * Acceso a pacientes y medicos, necesario para comprobar que
     * existen antes de agendar.
     *
     * Siguen construyendose a mano porque esos dos modulos todavia no
     * se han migrado a Spring. Cuando se migren, pasaran a recibirse
     * por el constructor igual que CitaDAO y estas dos lineas
     * desaparecen.
     */
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    /**
     * Spring entrega el repositorio ya construido.
     *
     * Se recibe por constructor y no por campo porque asi la clase no
     * puede existir a medias: o se construye con todo lo que necesita,
     * o no se construye. Ademas deja ver de un vistazo de que depende.
     */
    @Autowired
    public CitaServicio(CitaDAO citaDAO) {
        this.citaDAO = citaDAO;
    }

    // ==================== Consultas ====================

    public List<Cita> listar() {
        return citaDAO.listarCitas();
    }

    /** Devuelve la cita o falla si no existe. */
    public Cita obtener(Integer idCita) {
        Cita cita = citaDAO.buscarPorId(idCita);
        if (cita == null) {
            throw new NoEncontradoExcepcion(
                    "No existe una cita con el número " + idCita + ".");
        }
        return cita;
    }

    /** Agenda del dia de un medico (HU-14). */
    public List<Cita> agendaDelDia(Integer idMedico, LocalDate fecha) {
        if (idMedico == null) {
            throw new DatosInvalidosExcepcion("Debe indicar el médico.");
        }
        if (fecha == null) {
            throw new DatosInvalidosExcepcion("Debe indicar la fecha.");
        }
        if (medicoDAO.buscarPorId(idMedico) == null) {
            throw new NoEncontradoExcepcion(
                    "No existe un médico con el número " + idMedico + ".");
        }
        return citaDAO.agendaDelDia(idMedico, fecha);
    }

    // ==================== Reglas ====================

    /**
     * Comprueba que la cita se pueda guardar con esos datos.
     *
     * Se deja publico a proposito: la API necesita revisar las reglas
     * por separado, porque antes tiene que juntar los errores de
     * formato de todos los campos. Las pantallas en cambio llaman
     * directo a guardar.
     */
    public void verificarAgendable(Integer idPaciente, Integer idMedico,
            LocalDate fecha, LocalTime hora, Integer idCitaExcluir) {

        if (idPaciente == null || idMedico == null) {
            throw new DatosInvalidosExcepcion(
                    "Debe seleccionar el paciente y el médico.");
        }
        if (fecha == null) {
            throw new DatosInvalidosExcepcion("Debe indicar la fecha de la cita.");
        }
        if (hora == null) {
            throw new DatosInvalidosExcepcion("Debe indicar la hora de la cita.");
        }

        if (pacienteDAO.buscarPorId(idPaciente) == null) {
            throw new NoEncontradoExcepcion(
                    "No existe un paciente con el número " + idPaciente + ".");
        }

        Medico medico = medicoDAO.buscarPorId(idMedico);
        if (medico == null) {
            throw new NoEncontradoExcepcion(
                    "No existe un médico con el número " + idMedico + ".");
        }

        // Regla: no se agenda hacia atras en el tiempo
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new ReglaDeNegocioExcepcion(
                    "No se puede agendar una cita en una fecha u hora que ya pasó.");
        }

        // Regla HU-12: el medico no puede tener dos citas vigentes a la
        // misma hora. Las canceladas no cuentan: su horario quedo libre.
        if (citaDAO.medicoOcupado(idMedico, fecha, hora, idCitaExcluir)) {
            throw new ReglaDeNegocioExcepcion(
                    "El médico " + medico.getNombres() + " " + medico.getApellidos()
                    + " ya tiene una cita agendada el " + fecha.format(FORMATO_FECHA)
                    + " a las " + hora.format(FORMATO_HORA) + ".");
        }
    }

    /**
     * Comprueba que la cita siga abierta.
     *
     * Una cita cancelada libero su horario y una atendida ya ocurrio:
     * modificar cualquiera de las dos falsearia el historial. Para esos
     * casos lo que corresponde es agendar una cita nueva.
     */
    public void verificarAbierta(Cita cita) {
        if (cita.getEstado() != null && !cita.getEstado().ocupaHorario()) {
            throw new ReglaDeNegocioExcepcion(
                    "No se puede modificar una cita "
                    + cita.getEstado().getEtiqueta().toLowerCase()
                    + ". Agende una cita nueva.");
        }
    }

    // ==================== Operaciones ====================

    /**
     * Guarda la cita: la crea si es nueva, la actualiza si ya existia.
     *
     * Devuelve true si la cita quedo reprogramada, para que quien
     * llame pueda avisarlo. Se considera reprogramada cuando cambio la
     * fecha o la hora de una cita que ya existia: ese estado permite
     * distinguir en el historial una cita movida de una agendada de
     * primera.
     */
    @Transactional
    public boolean guardar(Cita cita, Integer idPaciente, Integer idMedico) {

        boolean esNueva = (cita.getIdCita() == null);
        boolean seMovio = false;

        if (!esNueva) {
            Cita guardada = obtener(cita.getIdCita());
            verificarAbierta(guardada);
            seMovio = !Objects.equals(guardada.getFecha(), cita.getFecha())
                   || !Objects.equals(guardada.getHora(), cita.getHora());
        }

        verificarAgendable(idPaciente, idMedico, cita.getFecha(), cita.getHora(),
                cita.getIdCita());

        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        Medico medico = medicoDAO.buscarPorId(idMedico);
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        if (esNueva) {
            cita.setEstado(EstadoCita.PROGRAMADA);
            citaDAO.insertar(cita);
            LOG.info("Cita agendada, id " + cita.getIdCita());
        } else {
            if (seMovio) {
                cita.setEstado(EstadoCita.REPROGRAMADA);
            }
            citaDAO.actualizar(cita);
            LOG.info("Cita actualizada, id " + cita.getIdCita()
                    + (seMovio ? " (reprogramada)" : ""));
        }
        return seMovio;
    }

    @Transactional
    public Cita cancelar(Integer idCita) {
        return cambiarEstado(idCita, EstadoCita.CANCELADA);
    }

    @Transactional
    public Cita atender(Integer idCita) {
        return cambiarEstado(idCita, EstadoCita.ATENDIDA);
    }

    @Transactional
    public void eliminar(Integer idCita) {
        obtener(idCita);
        citaDAO.eliminar(idCita);
        LOG.info("Cita eliminada, id " + idCita);
    }

    /**
     * Cambia el estado de una cita abierta.
     *
     * No lleva @Transactional: es un metodo privado y Spring no puede
     * interceptarlo. La transaccion ya esta abierta por cancelar() o
     * atender(), que son los que lo llaman.
     *
     * Cancelar no borra: la cita conserva su registro y solo cambia de
     * estado, de modo que el historial queda completo y el horario del
     * medico vuelve a quedar libre.
     */
    private Cita cambiarEstado(Integer idCita, EstadoCita nuevoEstado) {
        Cita cita = obtener(idCita);

        if (nuevoEstado.equals(cita.getEstado())) {
            throw new ReglaDeNegocioExcepcion(
                    "La cita ya está marcada como "
                    + nuevoEstado.getEtiqueta().toLowerCase() + ".");
        }
        if (!cita.getEstado().ocupaHorario()) {
            throw new ReglaDeNegocioExcepcion(
                    "La cita está " + cita.getEstado().getEtiqueta().toLowerCase()
                    + " y ya no admite cambios de estado.");
        }

        citaDAO.cambiarEstado(idCita, nuevoEstado);
        LOG.info("Cita " + idCita + " marcada como " + nuevoEstado.name());
        return citaDAO.buscarPorId(idCita);
    }
}