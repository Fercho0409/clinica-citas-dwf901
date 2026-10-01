package sv.edu.udb.clinica.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Administra la fabrica de EntityManager de JPA.
 *
 * Los datos de conexion no estan escritos en persistence.xml:
 * se leen de db.properties, que esta fuera del control de versiones.
 * Asi se mantiene la misma decision de la Fase 1, ahora con JPA.
 */
public class JPAUtil {

    private static final String UNIDAD_PERSISTENCIA = "clinicaPU";

    private static EntityManagerFactory emf;

    private JPAUtil() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Devuelve la fabrica, creandola la primera vez que se pide.
     * Es costosa de construir, por eso se crea una sola vez.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(
                    UNIDAD_PERSISTENCIA, cargarPropiedades());
        }
        return emf;
    }

    /**
     * Devuelve un EntityManager nuevo. Quien lo pide es responsable
     * de cerrarlo cuando termine.
     */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Lee db.properties y arma el mapa de propiedades de conexion
     * que JPA espera.
     */
    private static Map<String, String> cargarPropiedades() {
        Properties archivo = new Properties();

        try (InputStream entrada = JPAUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontro db.properties. "
                        + "Copie db.properties.example como db.properties "
                        + "en src/main/resources y escriba la contrasena local.");
            }

            archivo.load(entrada);

        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer db.properties", ex);
        }

        Map<String, String> propiedades = new HashMap<>();
        propiedades.put("javax.persistence.jdbc.driver", archivo.getProperty("db.driver"));
        propiedades.put("javax.persistence.jdbc.url", archivo.getProperty("db.url"));
        propiedades.put("javax.persistence.jdbc.user", archivo.getProperty("db.usuario"));
        propiedades.put("javax.persistence.jdbc.password", archivo.getProperty("db.password"));

        return propiedades;
    }

    /**
     * Cierra la fabrica. Se llama cuando la aplicacion se detiene.
     */
    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}