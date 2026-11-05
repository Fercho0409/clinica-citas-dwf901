package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;
import sv.edu.udb.clinica.modelo.Medico;

/**
 * Lo que la API devuelve de un medico, tanto en el listado como en el
 * detalle.
 *
 * Deliberadamente no incluye el DUI. El directorio de medicos es la
 * parte de la API pensada para sistemas externos (HU-17: consultar
 * medicos y su disponibilidad) y para el portal del paciente, y
 * ninguno de ellos necesita el documento de identidad del medico para
 * nada: identificar a un medico se hace con idMedico. El DUI solo se
 * usa por dentro, para impedir que se registre dos veces la misma
 * persona.
 *
 * Telefono y correo si se publican porque son los de contacto
 * profesional en la clinica.
 */
public class MedicoRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idMedico;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private Integer idEspecialidad;
    private String especialidad;
    private String telefono;
    private String correo;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public MedicoRespuesta() {
    }

    public static MedicoRespuesta desde(Medico m) {
        if (m == null) {
            return null;
        }
        MedicoRespuesta r = new MedicoRespuesta();
        r.idMedico = m.getIdMedico();
        r.nombres = m.getNombres();
        r.apellidos = m.getApellidos();
        r.nombreCompleto = m.getNombreCompleto();
        r.telefono = m.getTelefono();
        r.correo = m.getCorreo();
        if (m.getEspecialidad() != null) {
            r.idEspecialidad = m.getEspecialidad().getIdEspecialidad();
            r.especialidad = m.getEspecialidad().getNombre();
        }
        return r;
    }

    // ==================== Accesores ====================

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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
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
}
