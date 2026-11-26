package sv.edu.udb.clinica.controlador;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Arma el texto de /error.xhtml segun como se llego ahi.
 *
 *  - Desde ManejadorExcepciones: ?ref=A1B2C3 (error no controlado) o
 *    ?tipo=sesion (la pantalla caduco).
 *  - Desde Tomcat, por las <error-page> de web.xml: el codigo HTTP viene en
 *    el atributo javax.servlet.error.status_code (404, 403, 500...).
 *
 * Nunca muestra el mensaje ni el rastro de la excepcion: eso queda solo en
 * el registro del servidor.
 */
@ManagedBean(name = "paginaErrorBean")
@RequestScoped
public class PaginaErrorBean {

    private int codigo;
    private String titulo;
    private String descripcion;
    private String referencia;
    private boolean sesionCaducada;

    @PostConstruct
    public void init() {
        ExternalContext externo = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, String> parametros = externo.getRequestParameterMap();
        Object estado = externo.getRequestMap().get("javax.servlet.error.status_code");

        referencia = soloAlfanumerico(parametros.get("ref"));
        sesionCaducada = "sesion".equals(parametros.get("tipo"));
        codigo = (estado instanceof Integer) ? (Integer) estado : 500;

        if (sesionCaducada) {
            codigo = 0;
            titulo = "La sesión expiró";
            descripcion = "La pantalla estuvo inactiva demasiado tiempo y los datos "
                    + "que tenía abiertos ya no son válidos. Vuelva a ingresar y "
                    + "repita la operación.";
            return;
        }

        switch (codigo) {
            case 403:
                titulo = "Acceso no permitido";
                descripcion = "Su perfil no tiene permiso para ver esta pantalla.";
                break;
            case 404:
                titulo = "Página no encontrada";
                descripcion = "La dirección que intentó abrir no existe. "
                        + "Use el menú para llegar a la pantalla que busca.";
                break;
            default:
                titulo = "Ocurrió un error inesperado";
                descripcion = "No se pudo completar la operación. Intente de nuevo; "
                        + "si el problema continúa, informe al administrador "
                        + "el código de referencia.";
                break;
        }
    }

    /** El codigo viene en la URL: se filtra para no reflejar texto arbitrario. */
    private String soloAlfanumerico(String valor) {
        if (valor == null || !valor.matches("[A-Za-z0-9]{1,12}")) {
            return null;
        }
        return valor;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getReferencia() {
        return referencia;
    }

    public boolean isSesionCaducada() {
        return sesionCaducada;
    }
}
