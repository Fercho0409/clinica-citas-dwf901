package sv.edu.udb.clinica.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import sv.edu.udb.clinica.modelo.Paciente;

@Named(value = "pacienteBean")
@SessionScoped
public class PacienteManagedBean implements Serializable {

    private Paciente paciente = new Paciente();
    private List<Paciente> listaPacientes;

    public PacienteManagedBean() {
        // Aquí puedes inicializar la carga de la lista de pacientes
    }

    public String guardarPaciente() {
        try {
            // Lógica para guardar usando JPA
            // Si todo sale bien, redirige al listado
            return "pacientes?faces-redirect=true";
        } catch (Exception e) {
            // Manejar error y avisar al usuario (corrección solicitada en la fase)
            return null;
        }
    }

    public String eliminarPaciente(int id) {
        // Lógica para eliminar
        return "pacientes?faces-redirect=true";
    }

    // Getters y Setters
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public void setListaPacientes(List<Paciente> listaPacientes) { this.listaPacientes = listaPacientes; }
}