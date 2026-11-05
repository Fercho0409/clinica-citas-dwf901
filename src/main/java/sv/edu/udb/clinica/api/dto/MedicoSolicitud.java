package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;

/**
 * Lo que la API recibe para registrar (POST) o actualizar (PUT) un
 * medico.
 *
 * La especialidad se indica por su numero (idEspecialidad); la lista
 * de numeros validos sale de GET /api/especialidades.
 *
 * El DUI es obligatorio al registrar. Al actualizar es opcional: la
 * API nunca devuelve el DUI de un medico (ver MedicoRespuesta), asi
 * que el cliente no tiene como reenviarlo. Si no viene, se conserva
 * el que ya esta guardado.
 */
public class MedicoSolicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombres;
    private String apellidos;
    private String dui;   // obligatorio en POST, opcional en PUT
    private Integer idEspecialidad;
    private String telefono;   // 0000-0000, opcional
    private String correo;   // opcional

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public MedicoSolicitud() {
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

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
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
