package sv.edu.udb.clinica.error;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Mensajes de las pantallas JSF, en un solo lugar.
 *
 * Antes cada Managed Bean tenia sus propios metodos info() y error(), y en
 * los catch se concatenaba ex.getMessage() al texto que ve el usuario. Eso
 * mostraba en pantalla cosas como "could not execute statement; SQL
 * [n/a]; constraint [uk_pacientes_dui]", que revelan nombres de tablas y no
 * le dicen nada util a la recepcionista.
 *
 * Ahora:
 *  - info(), aviso() y error() muestran un texto escrito por nosotros.
 *  - errorInesperado() escribe el detalle tecnico completo en el registro
 *    del servidor con un codigo de referencia, y en pantalla solo muestra
 *    que accion fallo y ese codigo. Con el codigo se encuentra el error
 *    exacto en el log de Tomcat.
 *
 * Es el mismo criterio que sigue ManejadorErrores en la API.
 */
public final class Mensajes {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica.web");

    private Mensajes() {
    }

    public static void info(String mensaje) {
        agregar(FacesMessage.SEVERITY_INFO, "Listo", mensaje);
    }

    public static void aviso(String mensaje) {
        agregar(FacesMessage.SEVERITY_WARN, "Atención", mensaje);
    }

    public static void error(String mensaje) {
        agregar(FacesMessage.SEVERITY_ERROR, "Atención", mensaje);
    }

    /**
     * Para los catch de errores que no se esperaban (base caida, fallo de
     * Hibernate, etc.).
     *
     *   catch (RuntimeException ex) {
     *       Mensajes.errorInesperado("No se pudo guardar la cita", ex);
     *   }
     *
     * En pantalla: "No se pudo guardar la cita. Intente de nuevo; si el
     * problema continúa, informe el código A1B2C3."
     */
    public static void errorInesperado(String accion, Throwable ex) {
        String referencia = registrar(accion, ex);
        agregar(FacesMessage.SEVERITY_ERROR, "Error",
                accion + ". Intente de nuevo; si el problema continúa, "
                + "informe el código " + referencia + ".");
    }

    /**
     * Escribe la excepcion completa en el registro del servidor y devuelve
     * un codigo corto para identificarla. Lo usa tambien ManejadorExcepciones.
     */
    public static String registrar(String contexto, Throwable ex) {
        String referencia = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        LOG.log(Level.SEVERE, "[ref " + referencia + "] " + contexto, ex);
        return referencia;
    }

    private static void agregar(FacesMessage.Severity severidad, String resumen, String detalle) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (contexto != null) {
            contexto.addMessage(null, new FacesMessage(severidad, resumen, detalle));
        }
    }
}
