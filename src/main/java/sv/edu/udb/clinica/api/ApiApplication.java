package sv.edu.udb.clinica.api;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import sv.edu.udb.clinica.api.error.ManejadorErrores;
import sv.edu.udb.clinica.api.recurso.CitaRecurso;
import sv.edu.udb.clinica.api.recurso.EspecialidadRecurso;
import sv.edu.udb.clinica.api.recurso.MedicoRecurso;
import sv.edu.udb.clinica.api.recurso.PacienteRecurso;
import sv.edu.udb.clinica.api.util.FiltroCors;

/**
 * Registro de todo lo que compone la API REST.
 *
 * Antes, el web.xml le pedia a Jersey que buscara por su cuenta las
 * clases anotadas con @Path y @Provider dentro del paquete
 * sv.edu.udb.clinica.api. Esa busqueda fallaba al arrancar con:
 *
 *     IllegalArgumentException: Unsupported class file major version 65
 *
 * El 65 es Java 21. Para encontrar las anotaciones, Jersey lee los
 * archivos .class con una copia interna de la herramienta ASM, y la
 * que trae la version 2.35 solo entiende hasta Java 17. Como el
 * proyecto compila con 21, no podia leer ninguna clase.
 *
 * Declarandolas aqui no hace falta escanear nada: Jersey recibe la
 * lista ya hecha. Ademas el arranque es mas rapido y queda a la vista,
 * en un solo archivo, de que se compone la API.
 *
 * El precio es que al agregar un recurso nuevo hay que anotarlo en
 * esta lista. Si un endpoint responde 404 aunque la clase exista y
 * tenga su @Path, lo primero que hay que revisar es si falta aqui.
 *
 * La ruta base (/api) no se declara con @ApplicationPath porque ya la
 * define el servlet-mapping del web.xml.
 */
public class ApiApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> clases = new HashSet<>();

        // Recursos: las rutas de la API
        clases.add(CitaRecurso.class);
        clases.add(PacienteRecurso.class);
        clases.add(MedicoRecurso.class);
        clases.add(EspecialidadRecurso.class);

        // Proveedores: manejo de errores y permisos de origen cruzado
        clases.add(ManejadorErrores.class);
        clases.add(FiltroCors.class);

        return clases;
    }
}