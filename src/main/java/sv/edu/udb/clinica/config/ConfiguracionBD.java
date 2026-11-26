package sv.edu.udb.clinica.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Datos de conexion a la base, leidos FUERA del codigo versionado (DT-07).
 *
 * El persistence.xml ya no lleva URL, usuario ni contrasena. Esta clase
 * los busca en este orden y se queda con el primero que encuentre para
 * cada dato:
 *
 *   1. Variables de entorno: CLINICA_DB_URL, CLINICA_DB_USUARIO,
 *      CLINICA_DB_PASSWORD. Es lo que se usa en el servidor de despliegue.
 *
 *   2. Un archivo externo cuya ruta se indica con la variable de entorno
 *      CLINICA_CONFIG (o la propiedad de sistema -Dclinica.config=...).
 *      Sirve para dejar el archivo fuera de la carpeta del proyecto.
 *
 *   3. db.properties en el classpath (src/main/resources/db.properties).
 *      Es lo comodo para desarrollar en NetBeans. Ese archivo esta en el
 *      .gitignore y NO se sube; el modelo es db.properties.example.
 *
 * Si al final falta algun dato, la aplicacion no arranca y el mensaje dice
 * exactamente que falta y donde ponerlo. La contrasena nunca se escribe
 * en el registro.
 *
 * El resultado se entrega como un mapa de propiedades JPA que JPAUtil le
 * pasa a Persistence.createEntityManagerFactory(). Si mas adelante Spring
 * arma el DataSource, puede leer los mismos valores desde aqui.
 */
public final class ConfiguracionBD {

    private static final Logger LOG = Logger.getLogger(ConfiguracionBD.class.getName());

    private static final String ARCHIVO_CLASSPATH = "db.properties";

    private static final String DRIVER_POR_DEFECTO = "com.mysql.cj.jdbc.Driver";

    private final String driver;
    private final String url;
    private final String usuario;
    private final String password;
    private final boolean mostrarSql;
    private final String origen;

    private ConfiguracionBD(Properties archivo, String origenArchivo) {
        this.driver = valor("CLINICA_DB_DRIVER", archivo, "db.driver", DRIVER_POR_DEFECTO);
        this.url = valor("CLINICA_DB_URL", archivo, "db.url", null);
        this.usuario = valor("CLINICA_DB_USUARIO", archivo, "db.usuario", null);
        // La contrasena puede quedar vacia a proposito (MySQL local sin clave),
        // por eso no pasa por valor(), que trata el texto vacio como ausente.
        String passEntorno = System.getenv("CLINICA_DB_PASSWORD");
        this.password = (passEntorno != null) ? passEntorno : archivo.getProperty("db.password");
        this.mostrarSql = Boolean.parseBoolean(
                valor("CLINICA_DB_MOSTRAR_SQL", archivo, "db.mostrarSql", "false"));
        this.origen = origenArchivo;
    }

    /** Lee la configuracion. Lanza IllegalStateException si falta algun dato. */
    public static ConfiguracionBD cargar() {
        Properties archivo = new Properties();
        String origenArchivo = cargarArchivo(archivo);

        ConfiguracionBD config = new ConfiguracionBD(archivo, origenArchivo);
        config.verificar();

        LOG.info("Configuracion de base de datos cargada desde: " + config.origen
                + " | url=" + config.url + " | usuario=" + config.usuario);
        return config;
    }

    /** Propiedades que se le agregan a la unidad de persistencia clinicaPU. */
    public Map<String, Object> comoPropiedadesJpa() {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.jdbc.driver", driver);
        props.put("javax.persistence.jdbc.url", url);
        props.put("javax.persistence.jdbc.user", usuario);
        props.put("javax.persistence.jdbc.password", password);
        props.put("hibernate.show_sql", String.valueOf(mostrarSql));
        props.put("hibernate.format_sql", String.valueOf(mostrarSql));
        return props;
    }

    // ---------------------------------------------------------------

    /**
     * Carga el archivo externo si se indico uno; si no, el db.properties del
     * classpath; si tampoco existe, deja las propiedades vacias (todo tendra
     * que venir de variables de entorno).
     */
    private static String cargarArchivo(Properties destino) {
        String rutaExterna = System.getenv("CLINICA_CONFIG");
        if (vacio(rutaExterna)) {
            rutaExterna = System.getProperty("clinica.config");
        }

        if (!vacio(rutaExterna)) {
            Path ruta = Paths.get(rutaExterna.trim());
            if (!Files.isReadable(ruta)) {
                throw new IllegalStateException(
                        "CLINICA_CONFIG apunta a " + ruta.toAbsolutePath()
                        + ", pero ese archivo no existe o no se puede leer.");
            }
            try (Reader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
                destino.load(lector);
                return "archivo externo " + ruta.toAbsolutePath();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer " + ruta.toAbsolutePath(), e);
            }
        }

        try (InputStream entrada = ConfiguracionBD.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_CLASSPATH)) {
            if (entrada != null) {
                destino.load(entrada);
                return ARCHIVO_CLASSPATH + " del classpath";
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO_CLASSPATH, e);
        }

        return "variables de entorno";
    }

    /** La variable de entorno manda sobre el archivo; el archivo, sobre el valor por defecto. */
    private static String valor(String variableEntorno, Properties archivo,
                                String claveArchivo, String porDefecto) {
        String desdeEntorno = System.getenv(variableEntorno);
        if (!vacio(desdeEntorno)) {
            return desdeEntorno.trim();
        }
        String desdeArchivo = archivo.getProperty(claveArchivo);
        if (!vacio(desdeArchivo)) {
            return desdeArchivo.trim();
        }
        return porDefecto;
    }

    private void verificar() {
        StringBuilder faltantes = new StringBuilder();
        if (vacio(url)) {
            faltantes.append(" db.url (o CLINICA_DB_URL)");
        }
        if (vacio(usuario)) {
            faltantes.append(" db.usuario (o CLINICA_DB_USUARIO)");
        }
        if (password == null) {
            faltantes.append(" db.password (o CLINICA_DB_PASSWORD)");
        }
        if (faltantes.length() > 0) {
            throw new IllegalStateException(
                    "Falta configuracion de la base de datos:" + faltantes
                    + ". Copie src/main/resources/db.properties.example como "
                    + "db.properties y complete los datos, o defina las "
                    + "variables de entorno. Se busco en: " + origen + ".");
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
