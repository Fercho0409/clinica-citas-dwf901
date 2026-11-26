package sv.edu.udb.clinica.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import sv.edu.udb.clinica.config.ConfiguracionBD;

/**
 * Fabrica unica de EntityManager para toda la aplicacion.
 *
 * Los datos de conexion no estan en el persistence.xml: se leen con
 * ConfiguracionBD (variables de entorno o db.properties, que no se versiona)
 * y se le pasan a JPA al crear la fabrica (DT-07).
 */
public class JPAUtil {

    private static final Logger LOG = Logger.getLogger(JPAUtil.class.getName());

    private static final String UNIDAD_PERSISTENCIA = "clinicaPU";
    private static EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            try {
                ConfiguracionBD config = ConfiguracionBD.cargar();
                emf = Persistence.createEntityManagerFactory(
                        UNIDAD_PERSISTENCIA, config.comoPropiedadesJpa());
            } catch (RuntimeException e) {
                // No se usa ExceptionInInitializerError: es un Error y se salta los
                // catch (RuntimeException) de los Managed Beans, dejando una pagina
                // en blanco. Asi el fallo llega al manejo central de errores.
                LOG.log(Level.SEVERE, "No se pudo iniciar la unidad de persistencia "
                        + UNIDAD_PERSISTENCIA + ". Revise db.properties y que el esquema "
                        + "de la base coincida con las entidades (hbm2ddl=validate).", e);
                throw new IllegalStateException(
                        "No se pudo conectar con la base de datos.", e);
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
