package sv.edu.udb.clinica.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entidad JPA: representa una fila de la tabla medico.
 * Contiene el objeto Especialidad completo para que las vistas puedan mostrar el nombre sin consultar de nuevo.
 */
@Entity
@Table(name = "medico") // Asegúrate de que el nombre de la tabla en tu base de datos sea este
public class Medico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico") // Ajusta el nombre de la columna si en tu BD se llama diferente
    private int idMedico;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "jvpm")
    private String jvpm;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo")
    private String correo;

    
    @ManyToOne
    @JoinColumn(name = "id_especialidad") 
    private Especialidad especialidad;

    public Medico() {
    }

    public Medico(int idMedico, String nombres, String apellidos, String jvpm,
                  String telefono, String correo, Especialidad especialidad) {
        this.idMedico = idMedico;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.jvpm = jvpm;
        this.telefono = telefono;
        this.correo = correo;
        this.especialidad = especialidad;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getJvpm() {
        return jvpm;
    }

    public void setJvpm(String jvpm) {
        this.jvpm = jvpm;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}