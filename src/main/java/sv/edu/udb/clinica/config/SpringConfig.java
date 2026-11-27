package sv.edu.udb.clinica.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para la aplicación.
 *
 * Sustituye al archivo XML de configuración que se usaba
 * tradicionalmente: la clase lleva @Configuration y Spring la lee al
 * arrancar. El web.xml solo necesita saber su nombre.
 *
 * @ComponentScan le indica en qué paquetes buscar los componentes que
 * debe administrar. Se nombran únicamente los paquetes que van a
 * tenerlos, en lugar del paquete raíz del proyecto, para que Spring no
 * recorra las vistas ni los controladores de JSF, que siguen
 * administrados por el servidor.
 *
 * Fase 4, etapa A: por ahora el contexto arranca vacío. En la etapa
 * siguiente se agregan la unidad de persistencia y el manejo de
 * transacciones, cuando los DAO pasen a ser repositorios.
 */
@Configuration
@ComponentScan({
    "sv.edu.udb.clinica.config",
    "sv.edu.udb.clinica.dao",
    "sv.edu.udb.clinica.servicio"
})
public class SpringConfig {
}