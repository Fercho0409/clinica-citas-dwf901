package sv.edu.udb.clinica.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validador JSF del correo electrónico.
 *
 * La regla vive en ReglasValidacion para que la API REST aplique
 * exactamente la misma comprobacion que las pantallas.
 */
@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {
        if (value == null) {
            return;
        }
        String error = ReglasValidacion.validarCorreo(value.toString());
        if (error != null) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error de validación", error));
        }
    }
}
