package sv.edu.udb.clinica.validator;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]{2,}(\\.[A-Za-z0-9-]{2,})*\\.[A-Za-z]{2,6}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }

        String email = value.toString().trim().toLowerCase();

        // 1. Validar sintaxis general
        if (!pattern.matcher(email).matches()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de validación",
                    "El correo ingresado no tiene un formato o dominio válido.");
            throw new ValidatorException(msg);
        }

        // 2. Rechazar proveedores de correo con una sola letra (ej: @h.com)
        String[] parts = email.split("@");
        if (parts.length == 2) {
            String domain = parts[1];
            String[] domainParts = domain.split("\\.");
            if (domainParts[0].length() < 2) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error de validación",
                        "El proveedor del correo (dominio) no es válido.");
                throw new ValidatorException(msg);
            }
        }
    }
}