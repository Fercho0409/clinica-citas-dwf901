package sv.edu.udb.clinica.config;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Puente entre Jersey y Spring.
 *
 * ===================================================================
 *  Por que existe esta clase
 * ===================================================================
 *
 * Las pantallas JSF reciben los componentes de Spring por inyeccion:
 * se declara el campo con @ManagedProperty y JSF lo rellena solo,
 * gracias al puente que se registro en el faces-config.xml.
 *
 * Los recursos REST no pueden hacer lo mismo. A ellos los construye
 * Jersey, que trae su propio sistema de inyeccion (HK2) y no conoce a
 * Spring. Existe una libreria que los conecta, jersey-spring, pero
 * arrastra dependencias que chocan con las versiones que este proyecto
 * necesita para seguir corriendo en Tomcat 9.
 *
 * La solucion es esta clase: un unico punto donde pedirle a Spring un
 * componente ya construido. No es inyeccion de dependencias, es
 * localizacion: la diferencia es que aqui la clase va a buscar lo que
 * necesita en lugar de recibirlo. Se usa solo en los recursos REST, y
 * en una sola linea por recurso, precisamente para dejar aislada esa
 * excepcion.
 *
 * Si el dia de mañana se migra a jakarta y a una version de Jersey que
 * se integre con Spring, esta clase desaparece y esas lineas pasan a
 * ser inyeccion normal.
 */
public final class Contexto {

    private Contexto() {
    }

    /**
     * Devuelve el componente que Spring administra para ese tipo.
     *
     * @throws IllegalStateException si Spring no arranco. Eso solo
     *         puede pasar si falta el listener en el web.xml, y es
     *         mejor que falle aqui con un mensaje claro que mas
     *         adelante con un error que no diga nada.
     */
    public static <T> T obtener(Class<T> tipo) {
        WebApplicationContext contexto = ContextLoader.getCurrentWebApplicationContext();
        if (contexto == null) {
            throw new IllegalStateException(
                    "El contexto de Spring no esta disponible. "
                    + "Revise que el web.xml registre ContextLoaderListener.");
        }
        return contexto.getBean(tipo);
    }
}