package sv.edu.udb.clinica.controller;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import sv.edu.udb.clinica.dao.UsuarioDAO;
import sv.edu.udb.clinica.entity.Usuario;

@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String clave;
    private Usuario usuarioLogueado;

    // Al marcarlo como transient, la serialización de la sesión lo ignorará sin problemas
    private transient UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String iniciarSesion() {
        try {
            if (usuario == null || usuario.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar usuario y contraseña"));
                return null;
            }

            // Asegurar inicialización si se deserializó la sesión
            if (usuarioDAO == null) {
                usuarioDAO = new UsuarioDAO();
            }

            Usuario user = usuarioDAO.validarCredenciales(usuario.trim(), clave.trim());

            if (user != null) {
                this.usuarioLogueado = user;
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(true);
                session.setAttribute("usuarioLogueado", user);

                return "/views/pacientes/listado.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos"));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error inesperado", "Ocurrió un error al procesar la solicitud: " + e.getMessage()));
            return null;
        }
    }

    public String cerrarSesion() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        this.usuarioLogueado = null;
        this.usuario = null;
        this.clave = null;
        return "/login.xhtml?faces-redirect=true";
    }

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreUsuario() { return usuario; }
    public void setNombreUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Usuario usuarioLogueado) { this.usuarioLogueado = usuarioLogueado; }
}