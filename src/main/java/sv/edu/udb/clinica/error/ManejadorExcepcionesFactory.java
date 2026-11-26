package sv.edu.udb.clinica.error;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Fabrica que JSF usa para obtener el manejador de excepciones.
 * Envuelve el manejador por defecto con ManejadorExcepciones.
 * Registrada en WEB-INF/faces-config.xml.
 */
public class ManejadorExcepcionesFactory extends ExceptionHandlerFactory {

    private final ExceptionHandlerFactory padre;

    public ManejadorExcepcionesFactory(ExceptionHandlerFactory padre) {
        this.padre = padre;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new ManejadorExcepciones(padre.getExceptionHandler());
    }
}
