package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;

/**
 * Respuesta uniforme para cuando algo sale mal.
 *
 * Sin esto, cada endpoint termina devolviendo el error a su manera:
 * uno con texto suelto, otro con una pagina de error del servidor,
 * otro con el rastro completo de la excepcion. Quien consume la API
 * no tiene forma de saber que esperar.
 *
 * Con una sola clase, cualquier fallo se ve igual:
 *
 *   { "codigo": 409,
 *     "error": "Conflicto",
 *     "mensaje": "Ese medico ya tiene una cita el 20/10/2026 a las 09:00." }
 *
 * El mensaje debe poder mostrarse tal cual al usuario final. Nunca
 * se pone aqui el detalle tecnico de una excepcion: eso va al
 * registro del servidor, no hacia afuera.
 */
public class ErrorRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private int codigo;
    private String error;
    private String mensaje;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public ErrorRespuesta() {
    }

    public ErrorRespuesta(int codigo, String error, String mensaje) {
        this.codigo = codigo;
        this.error = error;
        this.mensaje = mensaje;
    }

    // ===== Atajos para los casos que se repiten =====

    /** 400: lo que mando el cliente esta mal formado o incompleto. */
    public static ErrorRespuesta solicitudInvalida(String mensaje) {
        return new ErrorRespuesta(400, "Solicitud invalida", mensaje);
    }

    /** 404: se pidio algo que no existe. */
    public static ErrorRespuesta noEncontrado(String mensaje) {
        return new ErrorRespuesta(404, "No encontrado", mensaje);
    }

    /**
     * 409: la peticion esta bien escrita, pero choca con una regla
     * de negocio. Es el caso del medico que ya tiene esa hora
     * ocupada, o de la cita en una fecha que ya paso.
     */
    public static ErrorRespuesta conflicto(String mensaje) {
        return new ErrorRespuesta(409, "Conflicto", mensaje);
    }

    /** 500: fallo algo de nuestro lado. */
    public static ErrorRespuesta errorInterno(String mensaje) {
        return new ErrorRespuesta(500, "Error interno", mensaje);
    }

    // ==================== Accesores ====================

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
