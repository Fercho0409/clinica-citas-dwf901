package sv.edu.udb.clinica.validator;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("telefonoValidator")
public class TelefonoValidator implements Validator {

    // Formato salvadoreño: inicia con 2, 6, 7 u 8, seguido de 3 dígitos, guion y 4 dígitos (ej: 7012-3456)
    private static final String TELEFONO_PATTERN = "^[2678]\\d{3}-\\d{4}$";
    private static final Pattern pattern = Pattern.compile(TELEFONO_PATTERN);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().trim().isEmpty()) {
            return; // Si no es obligatorio, permite vacío. Si es requerido, se encarga required="true".
        }

        String telefono = value.toString().trim();

        if (!pattern.matcher(telefono).matches()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de validación",
                    "El formato del teléfono debe ser 0000-0000.");
            throw new ValidatorException(msg);
        }
    }
}