package sv.edu.udb.clinica.controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.util.JPAUtil;

/**
 * Servlet temporal para verificar que JPA quedo bien configurado.
 * Se elimina cuando la Fase 2 este terminada.
 */
@WebServlet("/prueba-jpa")
public class PruebaJPAServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter salida = response.getWriter()) {
            salida.println("<html><head><meta charset='UTF-8'>");
            salida.println("<title>Prueba JPA</title></head><body>");
            salida.println("<h1>Prueba de conexion con JPA</h1>");

            EntityManager em = null;
            try {
                em = JPAUtil.getEntityManager();

                // Consulta JPQL: se escribe sobre la ENTIDAD (Especialidad),
                // no sobre la tabla. Hibernate la traduce a SQL.
                List<Especialidad> lista = em.createQuery(
                        "SELECT e FROM Especialidad e ORDER BY e.nombre",
                        Especialidad.class).getResultList();

                salida.println("<p style='color:green'><b>Conexion correcta.</b></p>");
                salida.println("<p>Especialidades encontradas: " + lista.size() + "</p>");
                salida.println("<ul>");
                for (Especialidad especialidad : lista) {
                    salida.println("<li>"
                            + especialidad.getIdEspecialidad() + " - "
                            + especialidad.getNombre() + " ("
                            + especialidad.getDescripcion() + ")</li>");
                }
                salida.println("</ul>");

            } catch (Exception ex) {
                salida.println("<p style='color:red'><b>Fallo la conexion.</b></p>");
                salida.println("<p>" + ex.getMessage() + "</p>");
                salida.println("<pre style='font-size:11px'>");
                ex.printStackTrace(salida);
                salida.println("</pre>");
            } finally {
                if (em != null) {
                    em.close();
                }
            }

            salida.println("</body></html>");
        }
    }
}