package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Ficha completa de un paciente: GET /api/pacientes/{id}, y la
 * respuesta de POST y PUT.
 *
 * Incluye los datos de contacto y el DUI completo porque es la vista
 * de detalle, la que se usa para editar. El listado devuelve
 * PacienteResumen, que no los trae.
 */
public class PacienteRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idPaciente;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String dui;
    private String fechaNacimiento;   // dd/MM/yyyy
    private Integer edad;   // calculada, en anos cumplidos
    private String telefono;
    private String correo;
    private String direccion;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public PacienteRespuesta() {
    }

    public static PacienteRespuesta desde(Paciente p) {
        if (p == null) {
            return null;
        }
        PacienteRespuesta r = new PacienteRespuesta();
        r.idPaciente = p.getIdPaciente();
        r.nombres = p.getNombres();
        r.apellidos = p.getApellidos();
        r.nombreCompleto = p.getNombreCompleto();
        r.dui = p.getDui();
        r.fechaNacimiento = p.getFechaNacimiento() != null ? p.getFechaNacimientoTexto() : null;
        r.edad = calcularEdad(p.getFechaNacimiento());
        r.telefono = p.getTelefono();
        r.correo = p.getCorreo();
        r.direccion = p.getDireccion();
        return r;
    }

    static Integer calcularEdad(LocalDate nacimiento) {
        return nacimiento != null ? Period.between(nacimiento, LocalDate.now()).getYears() : null;
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

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
