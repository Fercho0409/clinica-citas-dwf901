package sv.edu.udb.clinica.controlador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.modelo.Medico;

/**
 * Controlador de la pantalla de medicos.
 * Reemplaza a MedicoServlet dentro del flujo JSF.
 */
@ManagedBean(name = "medicoBean")
@ViewScoped
public class MedicoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MedicoDAO medicoDAO = new MedicoDAO();

    private List<Medico> medicos = new ArrayList<>();
    private List<Medico> medicosFiltrados = new ArrayList<>();
    private List<Especialidad> especialidades = new ArrayList<>();

    private Medico medico = new Medico();
    private Integer idEspecialidadSeleccionada;

    private String filtroTexto;
    private Integer filtroEspecialidad;

    @PostConstruct
    public void init() {
        try {
            especialidades = medicoDAO.listarEspecialidades();
            cargar();
        } catch (RuntimeException ex) {
            error("No se pudieron cargar los medicos: " + ex.getMessage());
        }
    }

    private void cargar() {
        medicos = medicoDAO.listarMedicos();
        medicosFiltrados = new ArrayList<>(medicos);
    }

    public void filtrar() {
        medicosFiltrados = medicos.stream()
                .filter(this::coincideTexto)
                .filter(this::coincideEspecialidad)
                .collect(Collectors.toList());
    }

    private boolean coincideTexto(Medico m) {
        if (filtroTexto == null || filtroTexto.trim().isEmpty()) {
            return true;
        }
        String busqueda = filtroTexto.trim().toLowerCase();
        return m.getNombres().toLowerCase().contains(busqueda)
                || m.getApellidos().toLowerCase().contains(busqueda)
                || (m.getDui() != null && m.getDui().toLowerCase().contains(busqueda));
    }

    private boolean coincideEspecialidad(Medico m) {
        return filtroEspecialidad == null
                || (m.getEspecialidad() != null
                && java.util.Objects.equals(
                        m.getEspecialidad().getIdEspecialidad(), filtroEspecialidad));
    }

    public void limpiarFiltros() {
        filtroTexto = null;
        filtroEspecialidad = null;
        medicosFiltrados = new ArrayList<>(medicos);
    }

    public void nuevo() {
        medico = new Medico();
        idEspecialidadSeleccionada = null;
    }

    public void editar(Medico seleccionado) {
        medico = medicoDAO.buscarPorId(seleccionado.getIdMedico());
        if (medico == null) {
            medico = new Medico();
            error("El medico ya no existe.");
            return;
        }
        idEspecialidadSeleccionada = medico.getEspecialidad() != null
                ? medico.getEspecialidad().getIdEspecialidad() : null;
    }

    public void guardar() {

        if (vacio(medico.getNombres()) || vacio(medico.getApellidos())
                || vacio(medico.getDui())) {
            error("Los nombres, apellidos y DUI son obligatorios.");
            return;
        }
        if (idEspecialidadSeleccionada == null) {
            error("Debe seleccionar la especialidad.");
            return;
        }

        try {
            if (medicoDAO.existeDui(medico.getDui().trim(), medico.getIdMedico())) {
                error("Ya existe un medico registrado con el DUI "
                        + medico.getDui() + ".");
                return;
            }

            Especialidad especialidad = especialidades.stream()
                    .filter(e -> java.util.Objects.equals(
                            e.getIdEspecialidad(), idEspecialidadSeleccionada))
                    .findFirst().orElse(null);

            if (especialidad == null) {
                error("La especialidad seleccionada ya no existe.");
                return;
            }

            medico.setDui(medico.getDui().trim());
            medico.setEspecialidad(especialidad);

            if (medico.getIdMedico() == null) {
                medicoDAO.insertar(medico);
                info("Medico registrado correctamente.");
            } else {
                medicoDAO.actualizar(medico);
                info("Medico actualizado correctamente.");
            }
            cargar();
            filtrar();
            nuevo();
        } catch (RuntimeException ex) {
            error("No se pudo guardar el medico: " + ex.getMessage());
        }
    }

    public void eliminar(Medico seleccionado) {
        try {
            medicoDAO.eliminar(seleccionado.getIdMedico());
            info("Medico eliminado.");
            cargar();
            filtrar();
        } catch (RuntimeException ex) {
            error("No se pudo eliminar: es probable que el medico tenga citas registradas.");
        }
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private void info(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Listo", mensaje));
    }

    private void error(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atencion", mensaje));
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Medico> getMedicosFiltrados() {
        return medicosFiltrados;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Integer getIdEspecialidadSeleccionada() {
        return idEspecialidadSeleccionada;
    }

    public void setIdEspecialidadSeleccionada(Integer idEspecialidadSeleccionada) {
        this.idEspecialidadSeleccionada = idEspecialidadSeleccionada;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }

    public Integer getFiltroEspecialidad() {
        return filtroEspecialidad;
    }

    public void setFiltroEspecialidad(Integer filtroEspecialidad) {
        this.filtroEspecialidad = filtroEspecialidad;
    }
}
