package sv.edu.udb.clinica.controlador;

import java.io.IOException;
import java.sql.SQLException;
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

        try {
            switch (accion) {
                case "listar":
                    List<Paciente> listaPacientes = pacienteDAO.listarPacientes();
                    request.setAttribute("listaPacientes", listaPacientes);
                    request.getRequestDispatcher("/views/pacientes/listar.jsp").forward(request, response);
                    break;

                case "nuevo":
                    request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
                    break;

                case "editar":
                    int idEdit = Integer.parseInt(request.getParameter("id"));
                    Paciente pacienteAEditar = pacienteDAO.buscarPorId(idEdit); // Llamada corregida
                    request.setAttribute("paciente", pacienteAEditar);
                    request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
                    break;

                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    pacienteDAO.eliminar(idEliminar); // Llamada corregida
                    response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
                    break;
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
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
        String direccion = request.getParameter("direccion");

        Paciente paciente = new Paciente();
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        paciente.setDui(dui);
        
        if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
            paciente.setFechaNacimiento(LocalDate.parse(fechaNacStr));
        }
        
        paciente.setTelefono(telefono);
        paciente.setCorreo(correo);
        paciente.setDireccion(direccion);

        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                // Guardar nuevo registro
                pacienteDAO.insertar(paciente); // Llamada corregida
            } else {
                // Actualizar registro existente
                paciente.setIdPaciente(Integer.parseInt(idStr));
                pacienteDAO.actualizar(paciente); // Llamada corregida
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
    }
}