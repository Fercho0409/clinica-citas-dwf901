package sv.edu.udb.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.udb.clinica.util.ConexionBD;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Especialidad;

public class MedicoDAO {

    public List<Medico> listarMedicos() throws SQLException {
        List<Medico> lista = new ArrayList<>();
        // Hacemos JOIN con especialidades para traer el objeto completo como pide el POJO
        String sql = "SELECT m.id_medico, m.nombres, m.apellidos, m.jvpm, m.telefono, m.correo, " +
                     "m.id_especialidad, e.nombre AS nombre_especialidad " +
                     "FROM medicos m JOIN especialidades e ON m.id_especialidad = e.id_especialidad";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                Medico medico = new Medico();
                medico.setIdMedico(rs.getInt("id_medico"));
                medico.setNombres(rs.getString("nombres"));
                medico.setApellidos(rs.getString("apellidos"));
                medico.setJvpm(rs.getString("jvpm"));
                medico.setTelefono(rs.getString("telefono"));
                medico.setCorreo(rs.getString("correo"));
                
                // Mapeamos el objeto Especialidad completo
                Especialidad esp = new Especialidad();
                esp.setIdEspecialidad(rs.getInt("id_especialidad"));
                esp.setNombre(rs.getString("nombre_especialidad"));
                medico.setEspecialidad(esp);
                
                lista.add(medico);
            }
        }
        return lista;
    }

    public Medico buscarPorId(int idMedico) throws SQLException {
        Medico medico = null;
        String sql = "SELECT m.id_medico, m.nombres, m.apellidos, m.jvpm, m.telefono, m.correo, " +
                     "m.id_especialidad, e.nombre AS nombre_especialidad " +
                     "FROM medicos m JOIN especialidades e ON m.id_especialidad = e.id_especialidad " +
                     "WHERE m.id_medico = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, idMedico);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    medico = new Medico();
                    medico.setIdMedico(rs.getInt("id_medico"));
                    medico.setNombres(rs.getString("nombres"));
                    medico.setApellidos(rs.getString("apellidos"));
                    medico.setJvpm(rs.getString("jvpm"));
                    medico.setTelefono(rs.getString("telefono"));
                    medico.setCorreo(rs.getString("correo"));
                    
                    Especialidad esp = new Especialidad();
                    esp.setIdEspecialidad(rs.getInt("id_especialidad"));
                    esp.setNombre(rs.getString("nombre_especialidad"));
                    medico.setEspecialidad(esp);
                }
            }
        }
        return medico;
    }

    public boolean insertar(Medico medico) throws SQLException {
        String sql = "INSERT INTO medicos (nombres, apellidos, jvpm, telefono, correo, id_especialidad) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, medico.getNombres());
            pstmt.setString(2, medico.getApellidos());
            pstmt.setString(3, medico.getJvpm());
            pstmt.setString(4, medico.getTelefono());
            pstmt.setString(5, medico.getCorreo());
            
            if (medico.getEspecialidad() != null) {
                pstmt.setInt(6, medico.getEspecialidad().getIdEspecialidad());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Medico medico) throws SQLException {
        String sql = "UPDATE medicos SET nombres = ?, apellidos = ?, jvpm = ?, telefono = ?, correo = ?, id_especialidad = ? WHERE id_medico = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, medico.getNombres());
            pstmt.setString(2, medico.getApellidos());
            pstmt.setString(3, medico.getJvpm());
            pstmt.setString(4, medico.getTelefono());
            pstmt.setString(5, medico.getCorreo());
            
            if (medico.getEspecialidad() != null) {
                pstmt.setInt(6, medico.getEspecialidad().getIdEspecialidad());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(7, medico.getIdMedico());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idMedico) throws SQLException {
        String sql = "DELETE FROM medicos WHERE id_medico = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, idMedico);
            return pstmt.executeUpdate() > 0;
        }
    }
}