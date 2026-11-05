package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;

/**
 * Lo que la API recibe para registrar (POST) o actualizar (PUT) un
 * paciente.
 *
 * No trae idPaciente: en un POST lo genera la base y en un PUT viaja
 * en la ruta (/api/pacientes/{id}). Si el cliente lo manda en el
 * cuerpo, se ignora.
 *
 * Igual que en CitaSolicitud, la fecha llega como texto dd/MM/yyyy
 * para poder responder con un mensaje claro si viene mal escrita.
 */
public class PacienteSolicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombres;
    private String apellidos;
    private String dui;
    private String fechaNacimiento;   // dd/MM/yyyy, obligatoria
    private String telefono;   // 0000-0000, opcional
    private String correo;   // opcional
    private String direccion;   // opcional

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public PacienteSolicitud() {
    }

    // ==================== Accesores ====================

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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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
