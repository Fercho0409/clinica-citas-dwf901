package sv.edu.udb.clinica.controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.udb.clinica.util.ConexionBD;

/**
 * Servlet temporal para verificar que la conexion a MySQL funciona.
 * Se elimina cuando el modulo de pacientes este listo.
 *
 * Probar en: http://localhost:8080/clinica-citas/prueba-conexion
 */
@WebServlet("/prueba-conexion")
public class PruebaConexionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection conexion = ConexionBD.obtenerConexion()) {

            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<title>Prueba de conexion</title></head><body>");

            if (conexion != null && !conexion.isClosed()) {
                out.println("<h1>Conexion exitosa</h1>");
                out.println("<p>Base de datos: "
                        + conexion.getMetaData().getDatabaseProductName() + " "
                        + conexion.getMetaData().getDatabaseProductVersion() + "</p>");
            } else {
                out.println("<h1>No se pudo conectar</h1>");
            }

            out.println("</body></html>");

        } catch (Exception e) {
            throw new ServletException("Error al conectar con la base de datos", e);
        }
    }
}
