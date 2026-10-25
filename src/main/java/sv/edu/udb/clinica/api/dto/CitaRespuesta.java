package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;
import sv.edu.udb.clinica.modelo.Cita;

/**
 * Lo que la API devuelve cuando alguien consulta una cita.
 *
 * No se devuelve la entidad Cita directamente por tres razones:
 *
 * 1. Las relaciones con paciente y medico estan en carga diferida.
 *    Al convertir a JSON fuera de la transaccion, Hibernate intenta
 *    ir a buscarlas y falla.
 *
 * 2. La entidad arrastra datos que no tienen por que salir hacia
 *    afuera. Aqui se decide exactamente que se publica.
 *
 * 3. Si manana cambia un nombre de columna en la entidad, la API
 *    sigue respondiendo igual. Lo que ve quien consume el servicio
 *    queda separado de como guardamos los datos.
 *
 * La fecha y la hora salen como texto a proposito: los convertidores
 * a JSON no manejan LocalDate ni LocalTime sin configuracion extra, y
 * la entidad ya ofrece los metodos que los entregan formateados.
 */
public class CitaRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idCita;

    private Integer idPaciente;
    private String paciente;
    private String pacienteDui;

    private Integer idMedico;
    private String medico;
    private String especialidad;

    private String fecha;   // dd/MM/yyyy
    private String hora;    // HH:mm

    private String estado;          // PROGRAMADA, CANCELADA, ...
    private String estadoEtiqueta;  // Programada, Cancelada, ...

    private String motivo;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public CitaRespuesta() {
    }

    /**
     * Arma la respuesta a partir de la entidad.
     *
     * Se comprueba que paciente y medico no sean nulos porque una cita
     * mal formada no deberia tumbar toda la consulta.
     */
    public static CitaRespuesta desde(Cita cita) {
        if (cita == null) {
            return null;
        }

        CitaRespuesta r = new CitaRespuesta();
        r.idCita = cita.getIdCita();
        r.fecha = cita.getFechaTexto();
        r.hora = cita.getHoraTexto();
        r.motivo = cita.getMotivo();

        if (cita.getEstado() != null) {
            r.estado = cita.getEstado().name();
            r.estadoEtiqueta = cita.getEstado().getEtiqueta();
        }

        if (cita.getPaciente() != null) {
            r.idPaciente = cita.getPaciente().getIdPaciente();
            r.paciente = cita.getPaciente().getNombreCompleto();
            r.pacienteDui = cita.getPaciente().getDui();
        }

        if (cita.getMedico() != null) {
            r.idMedico = cita.getMedico().getIdMedico();
            r.medico = cita.getMedico().getNombres() + " "
                    + cita.getMedico().getApellidos();

            if (cita.getMedico().getEspecialidad() != null) {
                r.especialidad = cita.getMedico().getEspecialidad().getNombre();
            }
        }

        return r;
    }

    // ==================== Accesores ====================

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getPacienteDui() {
        return pacienteDui;
    }

    public void setPacienteDui(String pacienteDui) {
        this.pacienteDui = pacienteDui;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoEtiqueta() {
        return estadoEtiqueta;
    }

    public void setEstadoEtiqueta(String estadoEtiqueta) {
        this.estadoEtiqueta = estadoEtiqueta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
