package sv.edu.udb.clinica.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import sv.edu.udb.clinica.api.dto.ErrorRespuesta;
import sv.edu.udb.clinica.api.error.ApiExcepcion;

/**
 * Convierte el cuerpo JSON de una peticion en el objeto de solicitud.
 *
 * Los recursos reciben el cuerpo como texto y lo convierten aqui con
 * Jackson, en lugar de dejar que Jersey lo haga antes de entrar al
 * metodo. La razon: cuando Jersey falla al leer un JSON mal escrito,
 * responde con el mensaje interno de Jackson ("Unexpected character
 * ('}' (code 125)) at [Source: ...]"), en ingles y con detalle tecnico.
 * Haciendolo aqui, el cliente recibe un 400 con un mensaje que puede
 * mostrar al usuario.
 *
 * Los campos que el cliente mande de mas (por ejemplo idPaciente en
 * un POST) se ignoran: el numero lo asigna la base, no quien consume.
 */
public final class LectorJson {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private LectorJson() {
    }

    public static <T> T leer(String cuerpo, Class<T> tipo) {
        if (cuerpo == null || cuerpo.trim().isEmpty()) {
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "La solicitud no trae cuerpo. Envíe los datos en formato JSON."));
        }
        try {
            T objeto = MAPPER.readValue(cuerpo, tipo);
            if (objeto == null) {
                throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                        "El cuerpo de la solicitud no puede ser null."));
            }
            return objeto;
        } catch (InvalidFormatException ex) {
            // Tipo equivocado: "idEspecialidad": "abc"
            String campo = ex.getPath().isEmpty()
                    ? "?" : ex.getPath().get(ex.getPath().size() - 1).getFieldName();
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "El campo '" + campo + "' tiene un tipo de dato incorrecto."));
        } catch (JsonProcessingException ex) {
            throw new ApiExcepcion(ErrorRespuesta.solicitudInvalida(
                    "El cuerpo no es un JSON válido o no tiene la estructura esperada."));
        }
    }
}
