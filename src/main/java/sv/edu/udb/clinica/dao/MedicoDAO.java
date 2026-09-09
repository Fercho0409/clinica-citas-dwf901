package sv.edu.udb.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.util.ConexionBD;
import sv.edu.udb.clinica.modelo.Medico;

public class MedicoDAO {

    public List<Medico> listarMedicos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT * FROM medicos";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
             while(rs.next()) {
                 Medico medico = new Medico();
                medico.setIdMedico(rs.getInt("id_medico"));
                medico.setNombres(rs.getString("nombres"));
                medico.setApellidos(rs.getString("apellidos"));
                medico.setJvpm(rs.getString("jvpm")); // Cambiado de dui a jvpm
                medico.setTelefono(rs.getString("telefono"));
                medico.setCorreo(rs.getString("correo"));

               
                Especialidad esp = new Especialidad();
                esp.setIdEspecialidad(rs.getInt("id_especialidad"));

                medico.setEspecialidad(esp);
                lista.add(medico);                 
             }
        } catch (SQLException e) {
        }
        return lista;
    }
}