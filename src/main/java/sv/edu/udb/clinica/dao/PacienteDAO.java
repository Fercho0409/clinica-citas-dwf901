package sv.edu.udb.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.udb.clinica.util.ConexionBD;
import sv.edu.udb.clinica.modelo.Paciente;

public class PacienteDAO {

    public List<Paciente> listarPacientes() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM pacientes";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
             while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setIdPaciente(rs.getInt("id_paciente"));
                paciente.setNombres(rs.getString("nombres"));
                paciente.setApellidos(rs.getString("apellidos"));
                paciente.setDui(rs.getString("dui"));
                paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null);
                paciente.setTelefono(rs.getString("telefono"));
                paciente.setCorreo(rs.getString("correo"));
                paciente.setDireccion(rs.getString("direccion"));
                lista.add(paciente);
           
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}