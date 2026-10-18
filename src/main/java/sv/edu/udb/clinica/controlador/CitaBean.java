package sv.edu.udb.clinica.controlador;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import sv.edu.udb.clinica.dao.CitaDAO;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Controlador de la pantalla de citas.
 *
 * Reemplaza al Servlet de la Fase 1: recibe lo que hace el usuario,
 * aplica las reglas de negocio y le pide los datos a la capa DAO.
 *
 * ViewScoped: el bean vive mientras el usuario permanezca en la misma
 * pantalla, de modo que los filtros no se pierden entre peticiones AJAX.
 */
@ManagedBean(name = "citaBean")
@ViewScoped
public class CitaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final CitaDAO citaDAO = new CitaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    private List<Cita> citas = new ArrayList<>();
    private List<Cita> citasFiltradas = new ArrayList<>();
    private List<Paciente> pacientes = new ArrayList<>();
    private List<Medico> medicos = new ArrayList<>();

    private Cita cita = new Cita();
    private Integer idPacienteSeleccionado;
    private Integer idMedicoSeleccionado;
    private String horaStr;

    private String filtroTexto;
    private Integer filtroMedico;
    private LocalDate filtroFecha;
    private String filtroEstado;

    @PostConstruct
    public void init() {
        try {
            pacientes = pacienteDAO.listarPacientes();
            medicos = medicoDAO.listarMedicos();
            cargarCitas();
        } catch (RuntimeException ex) {
            error("No se pudieron cargar los datos: " + ex.getMessage());
        }
    }

    private void cargarCitas() {
        citas = citaDAO.listarCitas();
        citasFiltradas = new ArrayList<>(citas);
    }

    // ==================== Filtros (AJAX) ====================

    public void filtrar() {
        citasFiltradas = citas.stream()
                .filter(this::coincideTexto)
                .filter(this::coincideMedico)
                .filter(this::coincideFecha)
                .filter(this::coincideEstado)
                .collect(Collectors.toList());
    }

    private boolean coincideTexto(Cita c) {
        if (filtroTexto == null || filtroTexto.trim().isEmpty()) {
            return true;
        }
        String busqueda = filtroTexto.trim().toLowerCase();
        Paciente p = c.getPaciente();
        if (p == null) {
            return false;
        }
        return p.getNombreCompleto().toLowerCase().contains(busqueda)
                || (p.getDui() != null && p.getDui().toLowerCase().contains(busqueda));
    }

    private boolean coincideMedico(Cita c) {
        return filtroMedico == null
                || (c.getMedico() != null
                && Objects.equals(c.getMedico().getIdMedico(), filtroMedico));
    }

    private boolean coincideFecha(Cita c) {
        return filtroFecha == null || filtroFecha.equals(c.getFecha());
    }

    private boolean coincideEstado(Cita c) {
        return filtroEstado == null || filtroEstado.trim().isEmpty()
                || (c.getEstado() != null && c.getEstado().name().equals(filtroEstado));
    }

    public void limpiarFiltros() {
        filtroTexto = null;
        filtroMedico = null;
        filtroFecha = null;
        filtroEstado = null;
        citasFiltradas = new ArrayList<>(citas);
    }

    // ==================== Alta y edicion ====================

    public void nueva() {
        cita = new Cita();
        cita.setEstado(EstadoCita.PROGRAMADA);
        idPacienteSeleccionado = null;
        idMedicoSeleccionado = null;
        horaStr = null;
    }

    public void editar(Cita seleccionada) {
        cita = citaDAO.buscarPorId(seleccionada.getIdCita());
        if (cita == null) {
            cita = new Cita();
            error("La cita ya no existe.");
            return;
        }
        idPacienteSeleccionado = cita.getPaciente() != null
                ? cita.getPaciente().getIdPaciente() : null;
        idMedicoSeleccionado = cita.getMedico() != null
                ? cita.getMedico().getIdMedico() : null;
        horaStr = cita.getHora() != null ? cita.getHora().format(FORMATO_HORA) : null;
    }

    /**
     * Guarda la cita despues de aplicar las reglas de negocio.
     *
     * Las validaciones se ejecutan aqui, en el servidor, y no solo en el
     * navegador: una peticion enviada directamente se saltaria el
     * required de los campos.
     */
    public void guardar() {

        if (idPacienteSeleccionado == null || idMedicoSeleccionado == null) {
            error("Debe seleccionar el paciente y el medico.");
            return;
        }
        if (cita.getFecha() == null) {
            error("Debe indicar la fecha de la cita.");
            return;
        }

        LocalTime hora = interpretarHora(horaStr);
        if (hora == null) {
            error("La hora debe escribirse en formato HH:mm, por ejemplo 09:30.");
            return;
        }
        cita.setHora(hora);

        // Regla: no se agenda en una fecha y hora que ya paso
        LocalDateTime momento = LocalDateTime.of(cita.getFecha(), hora);
        if (momento.isBefore(LocalDateTime.now())) {
            error("No se puede agendar una cita en una fecha u hora que ya paso.");
            return;
        }

        // Regla HU-12: el medico no puede tener dos citas vigentes
        // a la misma hora
        if (citaDAO.medicoOcupado(idMedicoSeleccionado, cita.getFecha(),
                hora, cita.getIdCita())) {
            error("Ese medico ya tiene una cita agendada el "
                    + cita.getFechaTexto() + " a las " + horaStr + ".");
            return;
        }

        Paciente paciente = pacientes.stream()
                .filter(p -> Objects.equals(p.getIdPaciente(), idPacienteSeleccionado))
                .findFirst().orElse(null);
        Medico medico = medicos.stream()
                .filter(m -> Objects.equals(m.getIdMedico(), idMedicoSeleccionado))
                .findFirst().orElse(null);

        if (paciente == null || medico == null) {
            error("El paciente o el medico seleccionado ya no existe.");
            return;
        }

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        if (cita.getEstado() == null) {
            cita.setEstado(EstadoCita.PROGRAMADA);
        }

        try {
            if (cita.getIdCita() == null) {
                citaDAO.insertar(cita);
                info("Cita agendada correctamente.");
            } else {
                citaDAO.actualizar(cita);
                info("Cita actualizada correctamente.");
            }
            cargarCitas();
            filtrar();
            nueva();
        } catch (RuntimeException ex) {
            error("No se pudo guardar la cita: " + ex.getMessage());
        }
    }

    /** Convierte el texto de la hora, aceptando 9:30 y 09:30. */
    private LocalTime interpretarHora(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(texto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException ex) {
            try {
                return LocalTime.parse(texto.trim(),
                        DateTimeFormatter.ofPattern("H:mm"));
            } catch (DateTimeParseException ex2) {
                return null;
            }
        }
    }

    // ==================== Cambios de estado ====================

    public void cancelar(Cita seleccionada) {
        try {
            citaDAO.cambiarEstado(seleccionada.getIdCita(), EstadoCita.CANCELADA);
            info("Cita cancelada. El horario del medico queda libre.");
            cargarCitas();
            filtrar();
        } catch (RuntimeException ex) {
            error("No se pudo cancelar la cita: " + ex.getMessage());
        }
    }

    public void marcarAtendida(Cita seleccionada) {
        try {
            citaDAO.cambiarEstado(seleccionada.getIdCita(), EstadoCita.ATENDIDA);
            info("Cita marcada como atendida.");
            cargarCitas();
            filtrar();
        } catch (RuntimeException ex) {
            error("No se pudo actualizar la cita: " + ex.getMessage());
        }
    }

    public void eliminar(Cita seleccionada) {
        try {
            citaDAO.eliminar(seleccionada.getIdCita());
            info("Cita eliminada.");
            cargarCitas();
            filtrar();
        } catch (RuntimeException ex) {
            error("No se pudo eliminar la cita: " + ex.getMessage());
        }
    }

    /** Color del distintivo de estado en la tabla. */
    public String badgeEstado(EstadoCita estado) {
        if (estado == null) {
            return "badge-secondary";
        }
        switch (estado) {
            case PROGRAMADA:
                return "badge-primary";
            case REPROGRAMADA:
                return "badge-warning";
            case ATENDIDA:
                return "badge-success";
            case CANCELADA:
                return "badge-danger";
            default:
                return "badge-secondary";
        }
    }

    // ==================== Mensajes ====================

    private void info(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Listo", mensaje));
    }

    private void error(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atencion", mensaje));
    }

    // ==================== Accesores ====================

    public List<Cita> getCitas() {
        return citas;
    }

    public List<Cita> getCitasFiltradas() {
        return citasFiltradas;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Integer getIdPacienteSeleccionado() {
        return idPacienteSeleccionado;
    }

    public void setIdPacienteSeleccionado(Integer idPacienteSeleccionado) {
        this.idPacienteSeleccionado = idPacienteSeleccionado;
    }

    public Integer getIdMedicoSeleccionado() {
        return idMedicoSeleccionado;
    }

    public void setIdMedicoSeleccionado(Integer idMedicoSeleccionado) {
        this.idMedicoSeleccionado = idMedicoSeleccionado;
    }

    public String getHoraStr() {
        return horaStr;
    }

    public void setHoraStr(String horaStr) {
        this.horaStr = horaStr;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }

    public Integer getFiltroMedico() {
        return filtroMedico;
    }

    public void setFiltroMedico(Integer filtroMedico) {
        this.filtroMedico = filtroMedico;
    }

    public LocalDate getFiltroFecha() {
        return filtroFecha;
    }

    public void setFiltroFecha(LocalDate filtroFecha) {
        this.filtroFecha = filtroFecha;
    }

    public String getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }
}
