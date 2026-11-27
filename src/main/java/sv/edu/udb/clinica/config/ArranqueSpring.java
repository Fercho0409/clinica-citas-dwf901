package sv.edu.udb.clinica.config;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

/**
 * Componente de verificación del arranque de Spring.
 *
 * Su única función es dejar constancia en el registro del servidor de
 * que el contenedor levantó correctamente y con qué versión. Sirve
 * como comprobación rápida: si esta línea no aparece en la salida de
 * Tomcat, Spring no arrancó y cualquier otra cosa que dependa de él
 * tampoco va a funcionar.
 *
 * Que esta clase sea encontrada y construida sola es, además, la
 * demostración de que el escaneo de componentes está operando.
 */
@Component
public class ArranqueSpring {

    private static final Logger LOG = Logger.getLogger("sv.edu.udb.clinica");

    @PostConstruct
    public void avisar() {
        LOG.info("===========================================");
        LOG.info("  Spring arrancó correctamente");
        LOG.info("  Versión: " + SpringVersion.getVersion());
        LOG.info("===========================================");
    }
}