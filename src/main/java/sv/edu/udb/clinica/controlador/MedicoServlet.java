package sv.edu.udb.clinica.controlador;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.udb.clinica.dao.MedicoDAO;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.modelo.Medico;

@WebServlet("/medicos")
public class MedicoServlet extends HttpServlet {

    private MedicoDAO medicoDAO = new MedicoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "listar":
                    List<Medico> listaMedicos = medicoDAO.listarMedicos();
                    request.setAttribute("listaMedicos", listaMedicos);
                    request.getRequestDispatcher("/views/medicos/listar.jsp").forward(request, response);
                    break;

                case "nuevo":
                    request.getRequestDispatcher("/views/medicos/formulario.jsp").forward(request, response);
                    break;

                case "editar":
                    int idEdit = Integer.parseInt(request.getParameter("id"));
                    Medico medicoAEditar = medicoDAO.buscarPorId(idEdit); 
                    request.setAttribute("medico", medicoAEditar);
                    request.getRequestDispatcher("/views/medicos/formulario.jsp").forward(request, response);
                    break;

                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    medicoDAO.eliminar(idEliminar);
                    response.sendRedirect(request.getContextPath() + "/medicos?accion=listar");
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/medicos?accion=listar");
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().println("Error en el servidor: " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("idMedico");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String jvpm = request.getParameter("jvpm");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String idEspecialidadStr = request.getParameter("idEspecialidad");

        Medico medico = new Medico();
        medico.setNombres(nombres);
        medico.setApellidos(apellidos);
        medico.setJvpm(jvpm);
        medico.setTelefono(telefono);
        medico.setCorreo(correo);

        if (idEspecialidadStr != null && !idEspecialidadStr.isEmpty()) {
            Especialidad esp = new Especialidad();
            esp.setIdEspecialidad(Integer.parseInt(idEspecialidadStr));
            medico.setEspecialidad(esp);
        }

        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                // Guardar nuevo registro
                medicoDAO.insertar(medico);
            } else {
                // Actualizar registro existente
                medico.setIdMedico(Integer.parseInt(idStr));
                medicoDAO.actualizar(medico);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/medicos?accion=listar");
    }
}