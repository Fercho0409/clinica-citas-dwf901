package sv.edu.udb.clinica.modelo;

/**
 * Estados posibles de una cita.
 * Coinciden con la restriccion CHECK de la tabla citas.
 */
public enum EstadoCita {

    PROGRAMADA("Programada"),
    REPROGRAMADA("Reprogramada"),
    CANCELADA("Cancelada"),
    ATENDIDA("Atendida");

    private final String etiqueta;

    EstadoCita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario en las vistas. */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Una cita cancelada o atendida ya no ocupa el horario del medico.
     * De esto depende la regla de no agendar dos veces a la misma hora:
     * si una cita se cancela, ese espacio vuelve a quedar libre.
     */
    public boolean ocupaHorario() {
        return this == PROGRAMADA || this == REPROGRAMADA;
    }
}