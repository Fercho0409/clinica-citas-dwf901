package sv.edu.udb.clinica.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("fechaNacimientoValidator")
public class FechaNacimientoValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        LocalDate fechaNac = null;

        if (value instanceof Date) {
            fechaNac = ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (value instanceof LocalDate) {
            fechaNac = (LocalDate) value;
        }

        if (fechaNac != null) {
            LocalDate hoy = LocalDate.now();

            if (fechaNac.isAfter(hoy)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error de validación",
                        "La fecha de nacimiento no puede ser posterior al día de hoy.");
                throw new ValidatorException(msg);
            }
        }
    }
}