package sv.edu.udb.clinica.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Cita medica: relaciona a un paciente con un medico en una fecha y hora.
 *
 * Es la entidad central de la Fase 2. Las relaciones hacia Paciente y
 * Medico se declaran con ManyToOne porque un paciente puede tener muchas
 * citas y un medico tambien.
 */
@Entity
@Table(name = "citas")
public class Cita implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    /**
     * EAGER porque el nombre del paciente se muestra en cada fila del
     * listado. Con LAZY habria que mantener abierto el EntityManager
     * hasta que la vista termine de dibujarse.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    /**
     * EnumType.STRING guarda el nombre del estado y no su posicion.
     * Con ORDINAL, agregar un estado nuevo en medio de la lista
     * cambiaria el significado de los registros ya guardados.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(name = "motivo", length = 255)
    private String motivo;

    /**
     * La base lo asigna sola con CURRENT_TIMESTAMP. Se marca como no
     * insertable ni actualizable: si JPA lo incluyera en el INSERT
     * enviaria un valor vacio a una columna que no lo admite.
     */
    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public Cita() {
    }

    public Cita(Paciente paciente, Medico medico, LocalDate fecha,
                LocalTime hora, String motivo) {
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = EstadoCita.PROGRAMADA;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /** Fecha lista para mostrar en la vista, sin convertidores. */
    public String getFechaTexto() {
        return fecha != null ? fecha.format(FORMATO_FECHA) : "";
    }

    /** Hora lista para mostrar en la vista. */
    public String getHoraTexto() {
        return hora != null ? hora.format(FORMATO_HORA) : "";
    }

    /** Fecha y hora juntas, para comparar la cita con el momento actual. */
    public LocalDateTime getFechaHora() {
        if (fecha == null || hora == null) {
            return null;
        }
        return LocalDateTime.of(fecha, hora);
    }

    /** Indica si la cita ya paso. */
    public boolean isPasada() {
        LocalDateTime momento = getFechaHora();
        return momento != null && momento.isBefore(LocalDateTime.now());
    }

    /** Solo se puede cancelar o reprogramar una cita vigente. */
    public boolean isModificable() {
        return estado != null && estado.ocupaHorario() && !isPasada();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Cita)) {
            return false;
        }
        return Objects.equals(idCita, ((Cita) objeto).idCita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCita);
    }

    @Override
    public String toString() {
        return "Cita " + idCita + " - " + getFechaTexto() + " " + getHoraTexto();
    }
}
