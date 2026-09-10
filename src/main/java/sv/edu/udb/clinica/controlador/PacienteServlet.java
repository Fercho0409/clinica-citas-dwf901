package sv.edu.udb.clinica.controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sv.edu.udb.clinica.dao.PacienteDAO;
import sv.edu.udb.clinica.modelo.Paciente;

@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {

    private PacienteDAO pacienteDAO = new PacienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                List<Paciente> listaPacientes = pacienteDAO.listarPacientes();
                request.setAttribute("listaPacientes", listaPacientes);
                request.getRequestDispatcher("/views/pacientes/listado.jsp").forward(request, response);
                break;

            case "nuevo":
                request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
                break;

            case "editar":
                request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
                break;

            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("idPaciente");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String dui = request.getParameter("dui");
        String fechaNacStr = request.getParameter("fechaNacimiento");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");

        Paciente paciente = new Paciente();
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        paciente.setDui(dui);
        
        if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
            paciente.setFechaNacimiento(LocalDate.parse(fechaNacStr));
        }
        
        paciente.setTelefono(telefono);
        paciente.setCorreo(correo);

        if (idStr == null || idStr.trim().isEmpty()) {
            // Guardar nuevo registro
        } else {
            // Actualizar registro existente
            paciente.setIdPaciente(Integer.parseInt(idStr));
        }

        response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
    }
}