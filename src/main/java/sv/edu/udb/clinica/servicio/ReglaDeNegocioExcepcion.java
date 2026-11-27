package sv.edu.udb.clinica.servicio;

/**
 * La peticion estaba bien escrita y todo lo que menciona existe, pero
 * no se puede hacer porque una regla de la clinica lo impide: el
 * medico ya tiene esa hora ocupada, la fecha ya paso, o la cita esta
 * cerrada y no admite cambios.
 *
 * La diferencia con DatosInvalidosExcepcion importa: alli el remedio
 * es corregir lo que se escribio; aqui no hay nada que corregir, lo
 * que se pide simplemente no esta permitido.
 *
 * En la API se traduce al codigo 409.
 */
public class ReglaDeNegocioExcepcion extends ServicioExcepcion {

    private static final long serialVersionUID = 1L;

    public ReglaDeNegocioExcepcion(String mensaje) {
        super(mensaje);
    }
}