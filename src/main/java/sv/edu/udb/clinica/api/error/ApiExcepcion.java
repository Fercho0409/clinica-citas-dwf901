package sv.edu.udb.clinica.api.error;

import sv.edu.udb.clinica.api.dto.ErrorRespuesta;

/**
 * Excepcion que lanzan los recursos REST cuando una peticion no puede
 * atenderse: datos invalidos, registro inexistente, regla de negocio.
 *
 * Lleva adentro la ErrorRespuesta ya armada. ManejadorErrores la
 * convierte en la respuesta HTTP, de modo que ningun endpoint tiene
 * que construir a mano un Response de error.
 *
 *   throw new ApiExcepcion(ErrorRespuesta.noEncontrado("No existe el paciente 15."));
 */
public class ApiExcepcion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient ErrorRespuesta error;

    public ApiExcepcion(ErrorRespuesta error) {
        super(error.getMensaje());
        this.error = error;
    }

    public ErrorRespuesta getError() {
        return error;
    }
}
