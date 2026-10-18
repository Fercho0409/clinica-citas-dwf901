package sv.edu.udb.clinica.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "usuarios")
@NamedQueries({
    @NamedQuery(
        name = "Usuario.login",
        query = "SELECT u FROM Usuario u WHERE u.nombreUsuario = :usuario AND u.clave = :clave AND u.activo = true"
    )
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "clave", nullable = false)
    private String clave;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Usuario() {
    }

    public Long getIdUsuario() { 
        return idUsuario; 
    }
    public void setIdUsuario(Long idUsuario) { 
        this.idUsuario = idUsuario; 
    }

    public String getNombreUsuario() { 
        return nombreUsuario; 
    }
    public void setNombreUsuario(String nombreUsuario) { 
        this.nombreUsuario = nombreUsuario; 
    }

    public String getClave() { 
        return clave; 
    }
    public void setClave(String clave) { 
        this.clave = clave; 
    }

    public String getNombreCompleto() { 
        return nombreCompleto; 
    }
    public void setNombreCompleto(String nombreCompleto) { 
        this.nombreCompleto = nombreCompleto; 
    }

    public String getRol() { 
        return rol; 
    }
    public void setRol(String rol) { 
        this.rol = rol; 
    }

    public Boolean getActivo() { 
        return activo; 
    }
    public void setActivo(Boolean activo) { 
        this.activo = activo; 
    }
}