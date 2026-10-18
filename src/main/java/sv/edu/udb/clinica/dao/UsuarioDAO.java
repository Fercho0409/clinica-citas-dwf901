package sv.edu.udb.clinica.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import sv.edu.udb.clinica.entity.Usuario;

public class UsuarioDAO {

    private static EntityManagerFactory emf;

    private synchronized static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("clinicaPU");
        }
        return emf;
    }

    public Usuario validarCredenciales(String username, String password) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createNamedQuery("Usuario.login", Usuario.class)
                     .setParameter("usuario", username)
                     .setParameter("clave", password)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}