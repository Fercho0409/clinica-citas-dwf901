package sv.edu.udb.clinica.servicio;

/**
 * Raiz de los errores que puede devolver la capa de servicio.
 *
 * ===================================================================
 *  Por que el servicio lanza excepciones y no devuelve mensajes
 * ===================================================================
 *
 * La capa de servicio la usan dos consumidores que no se parecen en
 * nada: las pantallas JSF, que muestran avisos en la parte superior de
 * la vista, y los servicios REST, que responden con un numero y un
 * texto en JSON.
 *
 * Si el servicio devolviera un mensaje listo para mostrar, estaria
 * decidiendo por ellos. Si devolviera un booleano, se perderia el
 * motivo del rechazo. La salida es lanzar una excepcion que diga que
 * paso y por que, y dejar que cada consumidor la traduzca a su propio
 * lenguaje:
 *
 *   ReglaDeNegocioExcepcion  ->  JSF: aviso en rojo
 *                            ->  API: codigo 409
 *
 *   NoEncontradoExcepcion    ->  JSF: "el registro ya no existe"
 *                            ->  API: codigo 404
 *
 *   DatosInvalidosExcepcion  ->  JSF: aviso en el formulario
 *                            ->  API: codigo 400
 *
 * Asi el servicio no sabe que existe HTTP ni que existe JSF, y puede
 * usarse mañana desde una tarea programada o desde una prueba
 * automatizada sin cambiarle una linea.
 *
 * Se extiende RuntimeException y no Exception a proposito: obligar a
 * capturar estos errores en cada llamada llenaria el codigo de
 * try/catch sin aportar nada, porque casi siempre lo que corresponde
 * es dejar que suban hasta quien sabe mostrarlos.
 */
public abstract class ServicioExcepcion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected ServicioExcepcion(String mensaje) {
        super(mensaje);
    }
}