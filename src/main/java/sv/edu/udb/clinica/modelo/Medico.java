package sv.edu.udb.clinica.modelo;

/**
 * POJO: representa una fila de la tabla medico.
 * Contiene el objeto Especialidad completo, no solo el id,
 * para que las vistas puedan mostrar el nombre sin consultar de nuevo.
 */
public class Medico {

    private int idMedico;
    private String nombres;
    private String apellidos;
    private String dui;
    private String telefono;
    private String correo;
    private Especialidad especialidad;

    public Medico() {
    }

    public Medico(int idMedico, String nombres, String apellidos, String dui,
                  String telefono, String correo, Especialidad especialidad) {
        this.idMedico = idMedico;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dui = dui;
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

    public String getdui() {
        return dui;
    }

    public void setdui(String dui) {
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

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
