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
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Controlador de la pantalla de pacientes.
 * Reemplaza a PacienteServlet dentro del flujo JSF.
 */
@ManagedBean(name = "pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    private List<Paciente> pacientes = new ArrayList<>();
    private List<Paciente> pacientesFiltrados = new ArrayList<>();
    private Paciente paciente = new Paciente();
    private String filtroTexto;

    @PostConstruct
    public void init() {
        try {
            cargar();
        } catch (RuntimeException ex) {
            error("No se pudieron cargar los pacientes: " + ex.getMessage());
        }
    }

    private void cargar() {
        pacientes = pacienteDAO.listarPacientes();
        pacientesFiltrados = new ArrayList<>(pacientes);
    }

    /** Busqueda sin recargar la pagina (HU-15). */
    public void filtrar() {
        if (filtroTexto == null || filtroTexto.trim().isEmpty()) {
            pacientesFiltrados = new ArrayList<>(pacientes);
            return;
        }
        String busqueda = filtroTexto.trim().toLowerCase();
        pacientesFiltrados = pacientes.stream()
                .filter(p -> p.getNombres().toLowerCase().contains(busqueda)
                        || p.getApellidos().toLowerCase().contains(busqueda)
                        || (p.getDui() != null && p.getDui().toLowerCase().contains(busqueda)))
                .collect(Collectors.toList());
    }

    public void limpiarFiltros() {
        filtroTexto = null;
        pacientesFiltrados = new ArrayList<>(pacientes);
    }

    public void nuevo() {
        paciente = new Paciente();
    }

    public void editar(Paciente seleccionado) {
        paciente = pacienteDAO.buscarPorId(seleccionado.getIdPaciente());
        if (paciente == null) {
            paciente = new Paciente();
            error("El paciente ya no existe.");
        }
    }

    public void guardar() {

        // Validacion de servidor: no se confia solo en el required del navegador
        if (vacio(paciente.getNombres()) || vacio(paciente.getApellidos())
                || vacio(paciente.getDui())) {
            error("Los nombres, apellidos y DUI son obligatorios.");
            return;
        }
        if (paciente.getFechaNacimiento() == null) {
            error("Debe indicar la fecha de nacimiento.");
            return;
        }

        try {
            // Se revisa el duplicado antes de guardar, en lugar de esperar
            // a que la base rechace la operacion
            if (pacienteDAO.existeDui(paciente.getDui().trim(), paciente.getIdPaciente())) {
                error("Ya existe un paciente registrado con el DUI "
                        + paciente.getDui() + ".");
                return;
            }

            paciente.setDui(paciente.getDui().trim());

            if (paciente.getIdPaciente() == null) {
                pacienteDAO.insertar(paciente);
                info("Paciente registrado correctamente.");
            } else {
                pacienteDAO.actualizar(paciente);
                info("Paciente actualizado correctamente.");
            }
            cargar();
            filtrar();
            nuevo();
        } catch (RuntimeException ex) {
            error("No se pudo guardar el paciente: " + ex.getMessage());
        }
    }

    public void eliminar(Paciente seleccionado) {
        try {
            pacienteDAO.eliminar(seleccionado.getIdPaciente());
            info("Paciente eliminado.");
            cargar();
            filtrar();
        } catch (RuntimeException ex) {
            // La llave foranea de citas impide borrar un paciente con historial
            error("No se pudo eliminar: es probable que el paciente tenga citas registradas.");
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

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Paciente> getPacientesFiltrados() {
        return pacientesFiltrados;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }
}
