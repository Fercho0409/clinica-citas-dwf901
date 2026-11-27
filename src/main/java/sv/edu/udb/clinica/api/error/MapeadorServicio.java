package sv.edu.udb.clinica.api.error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import sv.edu.udb.clinica.api.dto.ErrorRespuesta;
import sv.edu.udb.clinica.servicio.DatosInvalidosExcepcion;
import sv.edu.udb.clinica.servicio.NoEncontradoExcepcion;
import sv.edu.udb.clinica.servicio.ReglaDeNegocioExcepcion;
import sv.edu.udb.clinica.servicio.ServicioExcepcion;

/**
 * Traduce los avisos de la capa de servicio a respuestas HTTP.
 *
 * ===================================================================
 *  Por que hace falta
 * ===================================================================
 *
 * La capa de servicio no sabe que existe HTTP: cuando una regla no se
 * cumple, lanza una excepcion del dominio y se desentiende. Eso es lo
 * que permite que el mismo servicio atienda a las pantallas JSF y a la
 * API sin enterarse de la diferencia.
 *
 * Alguien tiene que hacer esa traduccion, y ese alguien es esta clase.
 * La correspondencia es directa:
 *
 *   DatosInvalidosExcepcion   ->  400  la peticion esta mal escrita
 *   NoEncontradoExcepcion     ->  404  no existe lo que se pide
 *   ReglaDeNegocioExcepcion   ->  409  esta bien escrita pero no se
 *                                      puede hacer
 *
 * La diferencia entre 400 y 409 importa y conviene tenerla clara: en
 * el primero hay algo que corregir en lo que se envio; en el segundo
 * no hay nada que corregir, simplemente no esta permitido.
 *
 * @Provider le dice a Jersey que esta clase atiende excepciones. Como
 * el escaneo automatico esta deshabilitado por la incidencia INC-31,
 * hay que anotarla ademas en ApiApplication o no se usa.
 */
@Provider
public class MapeadorServicio implements ExceptionMapper<ServicioExcepcion> {

    @Override
    public Response toResponse(ServicioExcepcion ex) {

        ErrorRespuesta cuerpo;

        if (ex instanceof NoEncontradoExcepcion) {
            cuerpo = ErrorRespuesta.noEncontrado(ex.getMessage());
        } else if (ex instanceof ReglaDeNegocioExcepcion) {
            cuerpo = ErrorRespuesta.conflicto(ex.getMessage());
        } else if (ex instanceof DatosInvalidosExcepcion) {
            cuerpo = ErrorRespuesta.solicitudInvalida(ex.getMessage());
        } else {
            // No deberia ocurrir: ServicioExcepcion es abstracta y solo
            // tiene esas tres hijas. Si aparece una cuarta sin
            // contemplar aqui, es preferible responder 409 antes que
            // dejar escapar un 500 sin explicacion.
            cuerpo = ErrorRespuesta.conflicto(ex.getMessage());
        }

        return Response.status(cuerpo.getCodigo())
                .entity(cuerpo)
                .type("application/json;charset=UTF-8")
                .build();
    }
}