package sv.edu.udb.clinica.validator;

import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("duiValidator")
public class DuiValidator implements Validator {

    // Expresión regular para el formato de DUI salvadoreño: 8 dígitos, un guion y 1 dígito verificador
    private static final String DUI_PATTERN = "^\\d{8}-\\d{1}$";
    private static final Pattern pattern = Pattern.compile(DUI_PATTERN);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        String dui = value.toString().trim();

        // 1. Validar formato con expresión regular
        if (!pattern.matcher(dui).matches()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de validación",
                    "El formato del DUI debe ser 00000000-0.");
            throw new ValidatorException(msg);
        }

        // 2. Validación opcional del dígito verificador módulo 10
        if (!isDuiValidChecksum(dui)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de validación",
                    "El número de DUI ingresado no es válido (dígito verificador incorrecto).");
            throw new ValidatorException(msg);
        }
    }

    private boolean isDuiValidChecksum(String dui) {
        try {
            String[] parts = dui.split("-");
            String digits = parts[0];
            int verifier = Integer.parseInt(parts[1]);

            int sum = 0;
            for (int i = 0; i < digits.length(); i++) {
                int digit = Character.getNumericValue(digits.charAt(i));
                sum += digit * (9 - i);
            }

            int mod = sum % 10;
            int calculatedVerifier = (10 - mod) % 10;

            return calculatedVerifier == verifier;
        } catch (Exception e) {
            return false;
        }
    }
}