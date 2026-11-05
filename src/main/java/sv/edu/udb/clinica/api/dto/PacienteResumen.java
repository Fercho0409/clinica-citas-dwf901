package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Lo que devuelve el listado de pacientes: GET /api/pacientes
 *
 * El listado entrega a la vez los datos de todos los pacientes, asi
 * que se recorta a lo minimo para identificar a la persona: nombre,
 * edad y los ultimos digitos del DUI (*****678-9). Telefono, correo,
 * direccion y DUI completo solo se entregan al pedir un paciente en
 * particular (PacienteRespuesta).
 *
 * Es el mismo criterio de un comprobante bancario que muestra solo
 * los ultimos digitos de la cuenta: suficiente para distinguir entre
 * dos personas con el mismo nombre, insuficiente para suplantar a
 * alguien si la lista se filtra.
 */
public class PacienteResumen implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idPaciente;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String duiParcial;
    private Integer edad;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public PacienteResumen() {
    }

    public static PacienteResumen desde(Paciente p) {
        if (p == null) {
            return null;
        }
        PacienteResumen r = new PacienteResumen();
        r.idPaciente = p.getIdPaciente();
        r.nombres = p.getNombres();
        r.apellidos = p.getApellidos();
        r.nombreCompleto = p.getNombreCompleto();
        r.duiParcial = enmascarar(p.getDui());
        r.edad = PacienteRespuesta.calcularEdad(p.getFechaNacimiento());
        return r;
    }

    /** 01234567-8  ->  *****567-8  (se dejan visibles los ultimos 5 caracteres) */
    static String enmascarar(String dui) {
        if (dui == null || dui.length() <= 5) {
            return dui;
        }
        int ocultos = dui.length() - 5;
        return "*".repeat(ocultos) + dui.substring(ocultos);
    }

    // ==================== Accesores ====================

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
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

    public String getDuiParcial() {
        return duiParcial;
    }

    public void setDuiParcial(String duiParcial) {
        this.duiParcial = duiParcial;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }
}
