package sv.edu.udb.clinica.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import sv.edu.udb.clinica.modelo.Usuario;
import sv.edu.udb.clinica.util.JPAUtil;

/**
 * Acceso a datos de usuarios.
 *
 * Usa la fabrica comun de JPAUtil en lugar de crear su propio
 * EntityManagerFactory (DT-14): antes se abrian dos fabricas y dos grupos
 * de conexiones contra la misma base.
 */
public class UsuarioDAO {

    /**
     * Devuelve el usuario activo con esas credenciales, o null si no coinciden.
     *
     * Un fallo de la base (por ejemplo, MySQL apagado) ya no se convierte en
     * null: se propaga, para que la pantalla no responda "usuario o contrasena
     * incorrectos" cuando el problema en realidad es la conexion.
     */
    public Usuario validarCredenciales(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Usuario.login", Usuario.class)
                     .setParameter("usuario", username)
                     .setParameter("clave", password)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
