package sv.edu.udb.clinica.validator;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Reglas de formato de los datos personales, sin depender de JSF.
 *
 * Hasta la Fase 2 estas reglas vivian dentro de los validadores JSF
 * (DuiValidator, EmailValidator, TelefonoValidator...), que solo
 * funcionan cuando hay un FacesContext. La API REST no pasa por JSF,
 * asi que las reglas se movieron aqui y ahora las usan los dos:
 * las pantallas y los servicios aplican exactamente la misma regla.
 *
 * Cada metodo devuelve null si el valor es valido, o el mensaje de
 * error listo para mostrarse al usuario.
 */
public final class ReglasValidacion {

    /** 8 digitos, guion y digito verificador: 00000000-0 */
    private static final Pattern DUI = Pattern.compile("^\\d{8}-\\d$");

    /** Telefono salvadoreno: inicia en 2, 6, 7 u 8. Formato 0000-0000 */
    private static final Pattern TELEFONO = Pattern.compile("^[2678]\\d{3}-\\d{4}$");

    private static final Pattern CORREO = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]{2,}(\\.[A-Za-z0-9-]{2,})*\\.[A-Za-z]{2,6}$");

    /** Nadie registrado en la clinica deberia tener mas de esta edad. */
    public static final int EDAD_MAXIMA = 120;

    private ReglasValidacion() {
    }

    // ==================== DUI ====================

    public static String validarDui(String dui) {
        if (dui == null || dui.trim().isEmpty()) {
            return "El DUI es obligatorio.";
        }
        String valor = dui.trim();
        if (!DUI.matcher(valor).matches()) {
            return "El formato del DUI debe ser 00000000-0.";
        }
        if (!digitoVerificadorValido(valor)) {
            return "El número de DUI ingresado no es válido (dígito verificador incorrecto).";
        }
        return null;
    }

    /**
     * Digito verificador del DUI: cada uno de los 8 digitos se
     * multiplica por su peso (9, 8, 7 ... 2), se suma, y el verificador
     * es (10 - suma mod 10) mod 10.
     */
    private static boolean digitoVerificadorValido(String dui) {
        String digitos = dui.substring(0, 8);
        int verificador = Character.getNumericValue(dui.charAt(9));

        int suma = 0;
        for (int i = 0; i < digitos.length(); i++) {
            suma += Character.getNumericValue(digitos.charAt(i)) * (9 - i);
        }
        return (10 - suma % 10) % 10 == verificador;
    }

    // ==================== Telefono ====================

    /** El telefono es opcional: vacio se considera valido. */
    public static String validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        if (!TELEFONO.matcher(telefono.trim()).matches()) {
            return "El formato del teléfono debe ser 0000-0000.";
        }
        return null;
    }

    // ==================== Correo ====================

    /** El correo es opcional: vacio se considera valido. */
    public static String validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        String valor = correo.trim().toLowerCase();
        if (!CORREO.matcher(valor).matches()) {
            return "El correo ingresado no tiene un formato o dominio válido.";
        }
        String dominio = valor.substring(valor.indexOf('@') + 1);
        if (dominio.split("\\.")[0].length() < 2) {
            return "El proveedor del correo (dominio) no es válido.";
        }
        return null;
    }

    // ==================== Fecha de nacimiento ====================

    /**
     * La fecha no puede ser futura. La API agrega ademas un limite
     * inferior (EDAD_MAXIMA) para atrapar errores de digitacion como
     * 1092 en lugar de 1992.
     */
    public static String validarFechaNacimiento(LocalDate fecha) {
        if (fecha == null) {
            return "La fecha de nacimiento es obligatoria.";
        }
        LocalDate hoy = LocalDate.now();
        if (fecha.isAfter(hoy)) {
            return "La fecha de nacimiento no puede ser posterior al día de hoy.";
        }
        if (fecha.isBefore(hoy.minusYears(EDAD_MAXIMA))) {
            return "La fecha de nacimiento indica más de " + EDAD_MAXIMA
                    + " años de edad. Revise el año.";
        }
        return null;
    }
}
