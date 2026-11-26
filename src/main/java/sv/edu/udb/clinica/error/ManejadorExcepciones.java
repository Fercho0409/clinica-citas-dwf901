package sv.edu.udb.clinica.error;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * Manejo centralizado de los errores de las pantallas JSF.
 *
 * Atiende toda excepcion que escape de un Managed Bean o de JSF mismo
 * (un metodo de accion que revienta, una expresion EL que falla, una vista
 * que caduco). Sin esto, JSF muestra su pagina de error con el rastro
 * completo de la excepcion, o en las peticiones AJAX simplemente no pasa
 * nada y el usuario no sabe que algo fallo.
 *
 * Hay dos casos:
 *
 *  1. ViewExpiredException: la pantalla estuvo abierta mas tiempo que la
 *     sesion (30 min). No es un fallo nuestro; se lleva al usuario a una
 *     pagina que se lo explica y le ofrece volver a entrar.
 *
 *  2. Cualquier otra: se registra con un codigo de referencia (ver
 *     Mensajes.registrar) y se redirige a /error.xhtml, que muestra un texto
 *     generico y ese codigo. El detalle tecnico solo queda en el log.
 *
 * ExternalContext.redirect() funciona igual en peticiones normales y AJAX:
 * en AJAX JSF responde con una instruccion de redireccion que el navegador
 * sigue.
 *
 * Se registra en WEB-INF/faces-config.xml a traves de
 * ManejadorExcepcionesFactory. Es el equivalente de ManejadorErrores para
 * la API REST.
 */
public class ManejadorExcepciones extends ExceptionHandlerWrapper {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.web");

    private final ExceptionHandler envuelto;

    public ManejadorExcepciones(ExceptionHandler envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return envuelto;
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> eventos = getUnhandledExceptionQueuedEvents().iterator();

        while (eventos.hasNext()) {
            ExceptionQueuedEventContext origen =
                    (ExceptionQueuedEventContext) eventos.next().getSource();
            Throwable excepcion = origen.getException();
            ViewExpiredException vistaCaducada = buscarVistaCaducada(excepcion);

            FacesContext contexto = FacesContext.getCurrentInstance();
            ExternalContext externo = contexto.getExternalContext();

            // Si ya se envio parte de la respuesta no se puede redirigir:
            // se deja que lo atienda el manejador por defecto.
            if (externo.isResponseCommitted()) {
                continue;
            }

            try {
                String destino;
                if (vistaCaducada != null) {
                    LOG.info("Vista caducada: " + vistaCaducada.getViewId());
                    destino = "/error.xhtml?tipo=sesion";
                } else {
                    String vista = contexto.getViewRoot() != null
                            ? contexto.getViewRoot().getViewId() : "(sin vista)";
                    String referencia = Mensajes.registrar("Error no controlado en " + vista, excepcion);
                    destino = "/error.xhtml?ref=" + referencia;
                }

                externo.redirect(externo.getRequestContextPath() + destino);
                contexto.responseComplete();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "No se pudo redirigir a la pagina de error", e);
            } finally {
                eventos.remove();
            }
        }

        getWrapped().handle();
    }

    /**
     * JSF suele envolver la excepcion original en FacesException o ELException,
     * asi que se recorre toda la cadena de causas buscando ViewExpiredException.
     * Al registrar se guarda la excepcion completa, con todas sus causas.
     */
    private ViewExpiredException buscarVistaCaducada(Throwable ex) {
        for (Throwable actual = ex; actual != null; actual = actual.getCause()) {
            if (actual instanceof ViewExpiredException) {
                return (ViewExpiredException) actual;
            }
            if (actual.getCause() == actual) {
                break;
            }
        }
        return null;
    }
}
