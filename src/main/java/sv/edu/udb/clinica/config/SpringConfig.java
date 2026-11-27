package sv.edu.udb.clinica.config;

import javax.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de Spring para la aplicación.
 *
 * Sustituye al archivo XML de configuración que se usaba
 * tradicionalmente: la clase lleva @Configuration y Spring la lee al
 * arrancar. El web.xml solo necesita saber su nombre.
 *
 * ===================================================================
 *  Qué hace cada anotación
 * ===================================================================
 *
 * @Configuration
 *      Marca la clase como fuente de configuración. Los métodos
 *      anotados con @Bean de aquí adentro producen objetos que Spring
 *      guarda y entrega a quien los necesite.
 *
 * @ComponentScan
 *      Le indica en qué paquetes buscar las clases anotadas con
 *      @Repository, @Service o @Component. Se nombran solo los
 *      paquetes que van a tenerlas, en lugar del paquete raíz, para
 *      que Spring no recorra las vistas ni los controladores de JSF,
 *      que siguen administrados por el servidor.
 *
 * @EnableTransactionManagement
 *      Activa el manejo de transacciones por anotación. A partir de
 *      aquí, un método marcado con @Transactional abre la transacción
 *      al entrar, la confirma al salir bien y la deshace si se lanza
 *      una excepción. Eso reemplaza el begin/commit/rollback que antes
 *      se escribía a mano en cada método de los DAO.
 */
@Configuration
@ComponentScan({
    "sv.edu.udb.clinica.config",
    "sv.edu.udb.clinica.dao",
    "sv.edu.udb.clinica.servicio"
})
@EnableTransactionManagement
public class SpringConfig {

    /**
     * Fábrica de conexiones a la base de datos.
     *
     * Se usa LocalEntityManagerFactoryBean, que lee el persistence.xml
     * tal como está y respeta la unidad "clinicaPU" con todas sus
     * propiedades. Se eligió esta forma, y no declarar la conexión
     * dentro de Spring, por dos razones:
     *
     *   1. El persistence.xml es responsabilidad de otra tarea de esta
     *      fase (sacar de ahí las credenciales). Al no reemplazarlo, los
     *      dos trabajos avanzan sin pisarse.
     *
     *   2. La configuración de la base sigue estando en un solo lugar.
     *      Si mañana cambia el servidor o la contraseña, se toca un
     *      archivo, no dos.
     *
     * Esta fábrica reemplaza a la que creaba JPAUtil a mano. La
     * diferencia es que Spring la construye una sola vez al arrancar,
     * la cierra sola al apagar la aplicación y se la entrega a quien la
     * pida, en lugar de que cada clase tenga que acordarse de pedirla y
     * de cerrarla.
     */
    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        LocalEntityManagerFactoryBean fabrica = new LocalEntityManagerFactoryBean();
        fabrica.setPersistenceUnitName("clinicaPU");
        return fabrica;
    }

    /**
     * Responsable de abrir, confirmar y deshacer las transacciones.
     *
     * Es lo que hace funcionar a @Transactional. Recibe la fábrica de
     * arriba: Spring se da cuenta solo de que debe pasársela, porque es
     * el único objeto de ese tipo que conoce. Eso es inyección de
     * dependencias: el método no va a buscar lo que necesita, se lo
     * entregan.
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory fabrica) {
        return new JpaTransactionManager(fabrica);
    }
}