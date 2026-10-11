package sv.edu.udb.clinica.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named(value = "citaBean")
@SessionScoped
public class CitaManagedBean implements Serializable {

    private int idPaciente;
    private int idMedico;
    private Date fechaCita;
    private String horaCita;
    private String estado;

    public CitaManagedBean() {
    }

    public String agendarCita() {
        // Regla 1: Validar que la fecha no sea pasada
        // Regla 2: Validar que no se le agende al mismo médico a la misma hora
        
        // Lógica de guardado JPA...
        return "citas?faces-redirect=true";
    }

    public String cancelarCita(int idCita) {
        // Lógica para cambiar el estado a cancelada
        return "citas?faces-redirect=true";
    }

    public String reprogramarCita() {
        // Lógica de reprogramación
        return "citas?faces-redirect=true";
    }

    // Getters y Setters
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public Date getFechaCita() { return fechaCita; }
    public void setFechaCita(Date fechaCita) { this.fechaCita = fechaCita; }

    public String getHoraCita() { return horaCita; }
    public void setHoraCita(String horaCita) { this.horaCita = horaCita; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}