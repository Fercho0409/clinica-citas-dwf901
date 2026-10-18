package sv.edu.udb.clinica.modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Medico de la clinica.
 *
 * El identificador es Integer y no Long: la columna id_medico de la base
 * es INT, y con Long Hibernate esperaba BIGINT y la validacion fallaba.
 *
 * Las columnas se llaman nombres y apellidos, en plural, igual que en
 * las demas tablas.
 */
@Entity
@Table(name = "medicos")
public class Medico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Integer idMedico;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "dui", nullable = false, unique = true, length = 10)
    private String dui;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    /**
     * EAGER porque el nombre de la especialidad se muestra siempre que
     * se lista un medico.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    public Medico() {
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
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

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
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

    /** Lo usan las vistas para mostrar el nombre en una sola columna. */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Medico)) {
            return false;
        }
        return Objects.equals(idMedico, ((Medico) objeto).idMedico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMedico);
    }

    @Override
    public String toString() {
        return "Dr. " + getNombreCompleto();
    }
}