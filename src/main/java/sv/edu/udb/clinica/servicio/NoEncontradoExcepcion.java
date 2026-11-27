package sv.edu.udb.clinica.servicio;

/**
 * Se pidio algo que no existe: una cita, un paciente o un medico con
 * un numero que no esta en la base.
 *
 * En la API se traduce al codigo 404.
 */
public class NoEncontradoExcepcion extends ServicioExcepcion {

    private static final long serialVersionUID = 1L;

    public NoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}