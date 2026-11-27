package sv.edu.udb.clinica.servicio;

/**
 * Los datos que llegaron no sirven: falta un campo obligatorio, o uno
 * de ellos no tiene la forma que corresponde.
 *
 * Es culpa de quien hizo la peticion y se corrige escribiendola bien.
 * En la API se traduce al codigo 400.
 */
public class DatosInvalidosExcepcion extends ServicioExcepcion {

    private static final long serialVersionUID = 1L;

    public DatosInvalidosExcepcion(String mensaje) {
        super(mensaje);
    }
}