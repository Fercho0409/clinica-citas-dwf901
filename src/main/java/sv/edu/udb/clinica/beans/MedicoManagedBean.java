package sv.edu.udb.clinica.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import sv.edu.udb.clinica.modelo.Medico;

@Named(value = "medicoBean")
@SessionScoped
public class MedicoManagedBean implements Serializable {

    private Medico medico = new Medico();
    private List<Medico> listaMedicos;

    public MedicoManagedBean() {
    }

    public String guardarMedico() {
        // Lógica para guardar médico (atendiendo el campo jvpm/dui)
        return "medicos?faces-redirect=true";
    }

    public String eliminarMedico(int id) {
        return "medicos?faces-redirect=true";
    }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public List<Medico> getListaMedicos() { return listaMedicos; }
    public void setListaMedicos(List<Medico> listaMedicos) { this.listaMedicos = listaMedicos; }
}