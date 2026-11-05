package sv.edu.udb.clinica.api.util;

import java.util.Map;

/**
 * Utilidades para validar los campos de una solicitud y acumular los
 * errores por campo. Las usan PacienteRecurso y MedicoRecurso.
 */
public final class Campos {

    private Campos() {
    }

    /** Recorta espacios; un texto vacio se trata como si no se hubiera enviado. */
    public static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String t = valor.trim();
        return t.isEmpty() ? null : t;
    }

    public static void obligatorio(Map<String, String> errores, String campo,
                                   String valor, int maximo) {
        if (valor == null) {
            errores.put(campo, "El campo " + campo + " es obligatorio.");
        } else if (valor.length() > maximo) {
            errores.put(campo, "El campo " + campo + " no puede superar "
                    + maximo + " caracteres.");
        }
    }

    /** Agrega el error solo si la regla devolvio un mensaje. */
    public static void anotar(Map<String, String> errores, String campo, String error) {
        if (error != null) {
            errores.put(campo, error);
        }
    }
}
