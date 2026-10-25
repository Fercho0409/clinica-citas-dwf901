package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;

/**
 * Lo que la API recibe cuando alguien quiere agendar o reprogramar
 * una cita.
 *
 * Es deliberadamente distinta de CitaRespuesta: quien agenda manda
 * los numeros de paciente y medico, no sus nombres, y nunca manda
 * el numero de cita, que lo genera la base de datos.
 *
 * La fecha y la hora llegan como texto, en el mismo formato en que
 * la API las devuelve. Asi quien consume el servicio no tiene que
 * aprender dos formatos distintos.
 *
 * Los campos quedan como texto sin interpretar a proposito: si
 * alguien manda una fecha mal escrita, se le responde con un mensaje
 * claro en lugar de que el error salte dentro del convertidor a JSON,
 * donde no se puede explicar nada.
 */
public class CitaSolicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idPaciente;
    private Integer idMedico;

    private String fecha;   // dd/MM/yyyy
    private String hora;    // HH:mm

    private String motivo;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public CitaSolicitud() {
    }

    // ==================== Accesores ====================

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
