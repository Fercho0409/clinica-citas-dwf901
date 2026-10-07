package sv.edu.udb.clinica.dao;

import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class PacienteDAO {

    public List<Paciente> listarPacientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Paciente buscarPorId(int idPaciente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Paciente.class, idPaciente);
        } finally {
            em.close();
        }
    }

    public boolean insertar(Paciente paciente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean actualizar(Paciente paciente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(paciente);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(int idPaciente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Paciente paciente = em.find(Paciente.class, idPaciente);
            if (paciente != null) {
                em.remove(paciente);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}