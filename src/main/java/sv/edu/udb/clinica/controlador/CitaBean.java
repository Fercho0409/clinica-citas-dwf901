package sv.edu.udb.clinica.controlador;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.servicio.CitaServicio;
import sv.edu.udb.clinica.servicio.ServicioExcepcion;

/**
 * Controlador de la pantalla de citas.
 *
 * Recibe lo que hace el usuario, se lo pide a la capa de servicio y
 * muestra el resultado.
 *
 * ===================================================================
 *  Que cambio en la Fase 4
 * ===================================================================
 *
 * Hasta la Fase 3 este archivo aplicaba las reglas de negocio por su
 * cuenta: comprobaba que la fecha no hubiera pasado, le preguntaba al
 * DAO si el medico estaba ocupado y decidia si guardar o no. Los
 * mismos controles, escritos otra vez, estaban en CitaRecurso para la
 * API.
 *
 * Ahora las reglas viven en CitaServicio y aqui no queda ninguna. La
 * pantalla se limita a lo suyo: recoger lo que el usuario escribio,
 * pedirle al servicio que lo guarde, y traducir el resultado a un
 * mensaje en pantalla.
 *
 * El beneficio concreto: si mañana la clinica decide que no se pueden
 * agendar citas los domingos, esa regla se escribe una vez y la
 * cumplen por igual las pantallas, la API y cualquier cosa que se
 * agregue despues.
 *
 * ViewScoped: el bean vive mientras el usuario permanezca en la misma
 * pantalla, de modo que los filtros no se pierden entre peticiones
 * AJAX.
 */
@ManagedBean(name = "citaBean")
@ViewScoped
public class CitaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * La capa de servicio, inyectada por Spring.
     *
     * Antes aqui habia tres lineas con "new CitaDAO()", "new
     * PacienteDAO()" y "new MedicoDAO()": la pantalla construia sus
     * propias herramientas y ademas sabia como usarlas. Ahora recibe
     * un solo colaborador, ya construido, que es el que sabe.
     *
     * @ManagedProperty es la forma de JSF de pedir una inyeccion. El
     * texto entre llaves es el nombre con el que Spring conoce al
     * componente, y funciona gracias al puente registrado en el
     * faces-config.xml. El campo no puede ser final porque quien lo
     * rellena es JSF, despues de construir el bean.
     */
    @ManagedProperty("#{citaServicio}")
    private CitaServicio citaServicio;

    /**
     * Estos dos siguen construyendose a mano porque solo sirven para
     * llenar los desplegables del formulario, y esos modulos todavia
     * no se migraron a Spring.
     */
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
        citas = citaServicio.listar();
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
        try {
            cita = citaServicio.obtener(seleccionada.getIdCita());
        } catch (ServicioExcepcion ex) {
            cita = new Cita();
            error(ex.getMessage());
            return;
        }
        idPacienteSeleccionado = cita.getPaciente() != null
                ? cita.getPaciente().getIdPaciente() : null;
        idMedicoSeleccionado = cita.getMedico() != null
                ? cita.getMedico().getIdMedico() : null;
        horaStr = cita.getHora() != null ? cita.getHora().format(FORMATO_HORA) : null;
    }

    /**
     * Guarda la cita.
     *
     * Lo unico que se valida aqui es el formato de la hora, porque eso
     * es cosa de la pantalla: el usuario la escribe como texto y hay
     * que convertirla antes de pasarsela a nadie. Todo lo demas
     * (campos obligatorios, fecha pasada, medico ocupado) lo decide el
     * servicio, y si algo no cumple lo avisa lanzando una excepcion
     * con el mensaje ya redactado.
     */
    public void guardar() {

        LocalTime hora = interpretarHora(horaStr);
        if (hora == null) {
            error("La hora debe escribirse en formato HH:mm, por ejemplo 09:30.");
            return;
        }
        cita.setHora(hora);

        try {
            boolean reprogramada = citaServicio.guardar(
                    cita, idPacienteSeleccionado, idMedicoSeleccionado);

            if (reprogramada) {
                info("Cita reprogramada correctamente.");
            } else {
                info("Cita guardada correctamente.");
            }
            cargarCitas();
            filtrar();
            nueva();

        } catch (ServicioExcepcion ex) {
            // Reglas de negocio y datos invalidos: el mensaje ya viene
            // redactado para mostrarse tal cual
            error(ex.getMessage());
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
            citaServicio.cancelar(seleccionada.getIdCita());
            info("Cita cancelada. El horario del médico queda libre.");
            cargarCitas();
            filtrar();
        } catch (ServicioExcepcion ex) {
            error(ex.getMessage());
        } catch (RuntimeException ex) {
            error("No se pudo cancelar la cita: " + ex.getMessage());
        }
    }

    public void marcarAtendida(Cita seleccionada) {
        try {
            citaServicio.atender(seleccionada.getIdCita());
            info("Cita marcada como atendida.");
            cargarCitas();
            filtrar();
        } catch (ServicioExcepcion ex) {
            error(ex.getMessage());
        } catch (RuntimeException ex) {
            error("No se pudo actualizar la cita: " + ex.getMessage());
        }
    }

    public void eliminar(Cita seleccionada) {
        try {
            citaServicio.eliminar(seleccionada.getIdCita());
            info("Cita eliminada.");
            cargarCitas();
            filtrar();
        } catch (ServicioExcepcion ex) {
            error(ex.getMessage());
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
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atención", mensaje));
    }

    // ==================== Accesores requeridos por la vista ====================

    /** Lo usa JSF para inyectar la capa de servicio. */
    public void setCitaServicio(CitaServicio citaServicio) {
        this.citaServicio = citaServicio;
    }

    public EstadoCita[] getEstadosCita() {
        return EstadoCita.values();
    }

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