package sv.edu.udb.clinica.controlador;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import sv.edu.udb.clinica.dao.CitaDAO;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;

/**
 * Controlador de la agenda diaria del medico (HU-14).
 *
 * Responde a una sola pregunta: que citas tiene este medico este dia.
 * La consulta ya existia en CitaDAO desde la Fase 2; lo unico que
 * faltaba era la pantalla que la invoca.
 *
 * Al abrir, muestra la agenda de hoy del primer medico de la lista,
 * de modo que el usuario vea algo util sin tener que elegir nada.
 */
@ManagedBean(name = "agendaBean")
@ViewScoped
public class AgendaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CitaDAO citaDAO = new CitaDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    private List<Medico> medicos = new ArrayList<>();
    private List<Cita> agenda = new ArrayList<>();

    private Integer idMedicoSeleccionado;
    private LocalDate fecha = LocalDate.now();

    @PostConstruct
    public void init() {
        try {
            medicos = medicoDAO.listarMedicos();

            if (!medicos.isEmpty()) {
                idMedicoSeleccionado = medicos.get(0).getIdMedico();
                buscar();
            }
        } catch (RuntimeException ex) {
            error("No se pudieron cargar los medicos: " + ex.getMessage());
        }
    }

    /** Carga la agenda del medico y la fecha seleccionados. */
    public void buscar() {
        if (idMedicoSeleccionado == null) {
            error("Debe seleccionar un medico.");
            agenda = new ArrayList<>();
            return;
        }
        if (fecha == null) {
            error("Debe indicar la fecha.");
            agenda = new ArrayList<>();
            return;
        }

        try {
            agenda = citaDAO.agendaDelDia(idMedicoSeleccionado, fecha);
        } catch (RuntimeException ex) {
            agenda = new ArrayList<>();
            error("No se pudo cargar la agenda: " + ex.getMessage());
        }
    }

    /** Lleva la agenda al dia de hoy. */
    public void hoy() {
        fecha = LocalDate.now();
        buscar();
    }

    /** Retrocede un dia. */
    public void diaAnterior() {
        if (fecha != null) {
            fecha = fecha.minusDays(1);
            buscar();
        }
    }

    /** Avanza un dia. */
    public void diaSiguiente() {
        if (fecha != null) {
            fecha = fecha.plusDays(1);
            buscar();
        }
    }

    // ==================== Datos para la vista ====================

    /** Fecha ya formateada para el encabezado de la pantalla. */
    public String getFechaTexto() {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "";
    }

    /** Nombre del medico seleccionado, para el encabezado. */
    public String getMedicoSeleccionadoNombre() {
        if (idMedicoSeleccionado == null) {
            return "";
        }
        for (Medico m : medicos) {
            if (idMedicoSeleccionado.equals(m.getIdMedico())) {
                return m.getNombres() + " " + m.getApellidos();
            }
        }
        return "";
    }

    /** Cuantas citas siguen vigentes ese dia. */
    public long getCantidadVigentes() {
        return agenda.stream()
                .filter(c -> c.getEstado() != null && c.getEstado().ocupaHorario())
                .count();
    }

    public long getCantidadAtendidas() {
        return agenda.stream()
                .filter(c -> EstadoCita.ATENDIDA.equals(c.getEstado()))
                .count();
    }

    public long getCantidadCanceladas() {
        return agenda.stream()
                .filter(c -> EstadoCita.CANCELADA.equals(c.getEstado()))
                .count();
    }

    /** Color del distintivo de estado en la tabla. */
    public String badgeEstado(EstadoCita estado) {
        if (estado == null) {
            return "badge badge-secondary";
        }
        switch (estado) {
            case PROGRAMADA:
                return "badge badge-primary";
            case REPROGRAMADA:
                return "badge badge-warning";
            case ATENDIDA:
                return "badge badge-success";
            case CANCELADA:
                return "badge badge-danger";
            default:
                return "badge badge-secondary";
        }
    }

    // ==================== Mensajes ====================

    private void error(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atencion", mensaje));
    }

    // ==================== Accesores ====================

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Cita> getAgenda() {
        return agenda;
    }

    public Integer getIdMedicoSeleccionado() {
        return idMedicoSeleccionado;
    }

    public void setIdMedicoSeleccionado(Integer idMedicoSeleccionado) {
        this.idMedicoSeleccionado = idMedicoSeleccionado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
