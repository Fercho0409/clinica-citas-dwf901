package sv.edu.udb.clinica.modelo;

import java.time.LocalDate;

/**
 * POJO: representa una fila de la tabla paciente.
 */
public class Paciente {

    private int idPaciente;
    private String nombres;
    private String apellidos;
    private String dui;
    private String telefono;
    private String correo;
    private LocalDate fechaNacimiento;

    public Paciente() {
    }

    public Paciente(int idPaciente, String nombres, String apellidos, String dui,
                    String telefono, String correo, LocalDate fechaNacimiento) {
        this.idPaciente = idPaciente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dui = dui;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /** Metodo de conveniencia para mostrar el nombre completo en las vistas. */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public void setDireccion(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

