package sv.edu.udb.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import sv.edu.udb.clinica.util.ConexionBD;
import sv.edu.udb.clinica.modelo.Paciente;

public class PacienteDAO {

    public List<Paciente> listarPacientes() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id_paciente, nombres, apellidos, dui, fecha_nacimiento, telefono, correo, direccion FROM pacientes";
        
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
        }
        return lista;
    }

    public Paciente buscarPorId(int idPaciente) throws SQLException {
        Paciente paciente = null;
        String sql = "SELECT id_paciente, nombres, apellidos, dui, fecha_nacimiento, telefono, correo, direccion FROM pacientes WHERE id_paciente = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, idPaciente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setIdPaciente(rs.getInt("id_paciente"));
                    paciente.setNombres(rs.getString("nombres"));
                    paciente.setApellidos(rs.getString("apellidos"));
                    paciente.setDui(rs.getString("dui"));
                    paciente.setFechaNacimiento(rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null);
                    paciente.setTelefono(rs.getString("telefono"));
                    paciente.setCorreo(rs.getString("correo"));
                    paciente.setDireccion(rs.getString("direccion"));
                }
            }
        }
        return paciente;
    }

    public boolean insertar(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO pacientes (nombres, apellidos, dui, fecha_nacimiento, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, paciente.getNombres());
            pstmt.setString(2, paciente.getApellidos());
            pstmt.setString(3, paciente.getDui());
            
            if (paciente.getFechaNacimiento() != null) {
                pstmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }
            
            pstmt.setString(5, paciente.getTelefono());
            pstmt.setString(6, paciente.getCorreo());
            pstmt.setString(7, paciente.getDireccion());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Paciente paciente) throws SQLException {
        String sql = "UPDATE pacientes SET nombres = ?, apellidos = ?, dui = ?, fecha_nacimiento = ?, telefono = ?, correo = ?, direccion = ? WHERE id_paciente = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, paciente.getNombres());
            pstmt.setString(2, paciente.getApellidos());
            pstmt.setString(3, paciente.getDui());
            
            if (paciente.getFechaNacimiento() != null) {
                pstmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }
            
            pstmt.setString(5, paciente.getTelefono());
            pstmt.setString(6, paciente.getCorreo());
            pstmt.setString(7, paciente.getDireccion());
            pstmt.setInt(8, paciente.getIdPaciente());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idPaciente) throws SQLException {
        String sql = "DELETE FROM pacientes WHERE id_paciente = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, idPaciente);
            return pstmt.executeUpdate() > 0;
        }
    }
}