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

/**
 * Controlador del modulo de pacientes.
 *
 * Recibe las peticiones, valida los parametros, delega en el DAO
 * y decide que vista mostrar. No contiene SQL ni logica de presentacion.
 */
@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {

    private final PacienteDAO pacienteDAO = new PacienteDAO();

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
                    listar(request, response);
                    break;

                case "nuevo":
                    // Formulario vacio para un registro nuevo
                    request.getRequestDispatcher("/views/pacientes/formulario.jsp")
                           .forward(request, response);
                    break;

                case "editar":
                    editar(request, response);
                    break;

                case "eliminar":
                    eliminar(request, response);
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
                    break;
            }

        } catch (SQLException e) {
            mostrarError(request, response,
                    "Ocurrio un error al acceder a la base de datos: " + e.getMessage());
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

        // Validacion del lado del servidor: los campos obligatorios
        // no pueden venir vacios aunque el navegador los deje pasar.
        if (esVacio(nombres) || esVacio(apellidos) || esVacio(dui)) {
            mostrarError(request, response,
                    "Los nombres, apellidos y DUI son obligatorios.");
            return;
        }

        Paciente paciente = new Paciente();
        paciente.setNombres(nombres.trim());
        paciente.setApellidos(apellidos.trim());
        paciente.setDui(dui.trim());
        paciente.setTelefono(telefono);
        paciente.setCorreo(correo);
        paciente.setDireccion(direccion);

        if (fechaNacStr != null && !fechaNacStr.isEmpty()) {
            try {
                paciente.setFechaNacimiento(LocalDate.parse(fechaNacStr));
            } catch (Exception e) {
                mostrarError(request, response, "La fecha de nacimiento no es valida.");
                return;
            }
        }

        try {
            boolean resultado;

            if (esVacio(idStr)) {
                // Sin id: es un registro nuevo
                resultado = pacienteDAO.insertar(paciente);
            } else {
                // Con id: se actualiza el existente
                paciente.setIdPaciente(Integer.parseInt(idStr));
                resultado = pacienteDAO.actualizar(paciente);
            }

            if (resultado) {
                // Patron POST-Redirect-GET: evita que al recargar
                // el navegador reenvie el formulario.
                response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");
            } else {
                mostrarError(request, response, "No se pudo guardar el paciente.");
            }

        } catch (NumberFormatException e) {
            mostrarError(request, response, "El identificador del paciente no es valido.");

        } catch (SQLException e) {
            // El DUI tiene restriccion UNIQUE en la base de datos
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                mostrarError(request, response,
                        "Ya existe un paciente registrado con el DUI " + dui + ".");
            } else {
                mostrarError(request, response,
                        "Ocurrio un error al guardar: " + e.getMessage());
            }
        }
    }

    // ---------- Acciones ----------

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<Paciente> listaPacientes = pacienteDAO.listarPacientes();
        request.setAttribute("listaPacientes", listaPacientes);
        request.getRequestDispatcher("/views/pacientes/listar.jsp")
               .forward(request, response);
    }

    private void editar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idStr = request.getParameter("id");

        if (esVacio(idStr)) {
            mostrarError(request, response, "No se indico que paciente editar.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            mostrarError(request, response, "El identificador del paciente no es valido.");
            return;
        }

        Paciente paciente = pacienteDAO.buscarPorId(id);

        if (paciente == null) {
            mostrarError(request, response, "No se encontro el paciente solicitado.");
            return;
        }

        // El formulario lee este atributo para precargar los campos
        request.setAttribute("paciente", paciente);
        request.getRequestDispatcher("/views/pacientes/formulario.jsp")
               .forward(request, response);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idStr = request.getParameter("id");

        if (esVacio(idStr)) {
            mostrarError(request, response, "No se indico que paciente eliminar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            pacienteDAO.eliminar(id);
            response.sendRedirect(request.getContextPath() + "/pacientes?accion=listar");

        } catch (NumberFormatException e) {
            mostrarError(request, response, "El identificador del paciente no es valido.");
        }
    }

    // ---------- Utilidades ----------

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response,
                              String mensaje) throws ServletException, IOException {

        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/views/mensajes/error.jsp")
               .forward(request, response);
    }
}