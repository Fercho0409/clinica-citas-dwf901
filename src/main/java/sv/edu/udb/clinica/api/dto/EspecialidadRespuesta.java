package sv.edu.udb.clinica.api.dto;

import java.io.Serializable;
import sv.edu.udb.clinica.modelo.Especialidad;

/**
 * Especialidad medica: GET /api/especialidades
 *
 * La necesita el cliente para llenar la lista desplegable del
 * formulario de medicos y para filtrar el directorio.
 */
public class EspecialidadRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idEspecialidad;
    private String nombre;
    private String descripcion;

    /** Constructor vacio: lo necesita el convertidor a JSON. */
    public EspecialidadRespuesta() {
    }

    public static EspecialidadRespuesta desde(Especialidad e) {
        if (e == null) {
            return null;
        }
        EspecialidadRespuesta r = new EspecialidadRespuesta();
        r.idEspecialidad = e.getIdEspecialidad();
        r.nombre = e.getNombre();
        r.descripcion = e.getDescripcion();
        return r;
    }

    // ==================== Accesores ====================

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
