package sv.edu.udb.clinica.api.error;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import sv.edu.udb.clinica.api.dto.ErrorRespuesta;

/**
 * Manejo centralizado de errores de toda la API.
 *
 * Cualquier excepcion que escape de un recurso termina aqui y sale
 * con el mismo formato JSON de ErrorRespuesta. Hay tres casos:
 *
 * 1. ApiExcepcion: error previsto (400, 404, 409). Se devuelve tal cual.
 *
 * 2. WebApplicationException: la lanza Jersey por su cuenta, por
 *    ejemplo ruta inexistente (404), metodo no permitido (405) o
 *    Content-Type distinto de JSON (415). Se conserva el codigo y se
 *    reemplaza la pagina HTML de Tomcat por un JSON con mensaje en
 *    espanol.
 *
 * 3. Cualquier otra cosa es un fallo nuestro (500). El detalle tecnico
 *    se escribe en el registro del servidor y hacia afuera solo sale
 *    un mensaje generico: el rastro de una excepcion revela nombres
 *    de tablas, clases y versiones.
 *
 * Se usa un solo mapeador para Throwable en lugar de uno por tipo para
 * no depender del orden en que Jersey elige entre varios candidatos.
 */
@Provider
public class ManejadorErrores implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.api");

    @Override
    public Response toResponse(Throwable ex) {

        if (ex instanceof ApiExcepcion) {
            ErrorRespuesta error = ((ApiExcepcion) ex).getError();
            LOG.log(Level.INFO, "API {0}: {1}",
                    new Object[]{error.getCodigo(), error.getMensaje()});
            return construir(error);
        }

        if (ex instanceof WebApplicationException) {
            int codigo = ((WebApplicationException) ex).getResponse().getStatus();
            LOG.log(Level.INFO, "API {0}: {1}", new Object[]{codigo, ex.getMessage()});
            return construir(desdeCodigo(codigo));
        }

        LOG.log(Level.SEVERE, "Error no controlado en la API", ex);
        return construir(ErrorRespuesta.errorInterno(
                "Ocurrió un error inesperado al procesar la solicitud. "
                + "Intente de nuevo más tarde."));
    }

    private Response construir(ErrorRespuesta error) {
        return Response.status(error.getCodigo())
                .type(MediaType.APPLICATION_JSON + ";charset=UTF-8")
                .entity(error)
                .build();
    }

    /** Mensajes para los errores que genera Jersey antes de llegar al recurso. */
    private ErrorRespuesta desdeCodigo(int codigo) {
        switch (codigo) {
            case 404:
                return ErrorRespuesta.noEncontrado("La ruta solicitada no existe en la API.");
            case 405:
                return new ErrorRespuesta(405, "Metodo no permitido",
                        "Esta ruta no admite el método HTTP utilizado.");
            case 406:
                return new ErrorRespuesta(406, "No aceptable",
                        "La API solo responde en formato JSON.");
            case 415:
                return new ErrorRespuesta(415, "Tipo de contenido no soportado",
                        "El cuerpo debe enviarse como JSON "
                        + "(encabezado Content-Type: application/json).");
            default:
                if (codigo >= 400 && codigo < 500) {
                    return new ErrorRespuesta(codigo, "Solicitud rechazada",
                            "La solicitud no pudo procesarse.");
                }
                return ErrorRespuesta.errorInterno(
                        "Ocurrió un error inesperado al procesar la solicitud.");
        }
    }
}
