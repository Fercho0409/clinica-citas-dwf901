package sv.edu.udb.clinica.api.util;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Permite que el cliente consumidor llame a la API desde otro origen.
 *
 * El cliente de la Fase 3 es una aplicacion aparte (React, Vue o
 * JavaScript) que normalmente corre en otro puerto, por ejemplo
 * http://localhost:5173, mientras la API esta en
 * http://localhost:8080/clinica-citas. El navegador bloquea esas
 * llamadas salvo que la API declare que las acepta.
 *
 * Se permite cualquier origen porque es un entorno de desarrollo. En
 * la Fase 4, al desplegar, debe restringirse al dominio del cliente.
 */
@Provider
public class FiltroCors implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta)
            throws IOException {
        respuesta.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        respuesta.getHeaders().putSingle("Access-Control-Allow-Methods",
                "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        respuesta.getHeaders().putSingle("Access-Control-Allow-Headers",
                "Content-Type, Accept, Authorization");
        respuesta.getHeaders().putSingle("Access-Control-Expose-Headers", "Location");
    }
}
