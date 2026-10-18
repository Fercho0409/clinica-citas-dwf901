package sv.edu.udb.clinica.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import sv.edu.udb.clinica.dao.EspecialidadDAO;
import sv.edu.udb.clinica.modelo.Especialidad;

@FacesConverter("especialidadConverter")
public class EspecialidadConverter implements Converter {

    private EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            return especialidadDAO.buscarPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Especialidad) {
            Especialidad esp = (Especialidad) value;
            return esp.getIdEspecialidad() != null ? esp.getIdEspecialidad().toString() : "";
        }
        return "";
    }
}