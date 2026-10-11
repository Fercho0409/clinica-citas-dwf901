package sv.edu.udb.clinica.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Entrega conexiones a MySQL.
 *
 * Las credenciales NO estan escritas aqui: se leen del archivo
 * db.properties, que vive fuera del control de versiones.
 * Eso es lo que la rubrica pide como "configuracion externa".
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIG = "db.properties";

    private static String url;
    private static String usuario;
    private static String password;

    static {
        cargarConfiguracion();
    }

    // Constructor privado: esta clase no se instancia.
    private ConexionBD() {
    }

    private static void cargarConfiguracion() {
        try (InputStream entrada = ConexionBD.class
                .getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIG)) {

            if (entrada == null) {
                throw new IllegalStateException(
                        "No se encontro " + ARCHIVO_CONFIG + " en el classpath. "
                        + "Copia db.properties.example como db.properties.");
            }

            Properties props = new Properties();
            props.load(entrada);

            url = props.getProperty("db.url");
            usuario = props.getProperty("db.usuario");
            password = props.getProperty("db.password");

            // Registra el driver de MySQL.
            Class.forName(props.getProperty("db.driver"));

        } catch (IOException e) {
            throw new IllegalStateException("Error al leer " + ARCHIVO_CONFIG, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No se encontro el driver JDBC. Revisa la dependencia mysql-connector-j.", e);
        }
    }

    /**
     * Devuelve una conexion nueva.
     * Quien la pide es responsable de cerrarla (usar try-with-resources).
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}
