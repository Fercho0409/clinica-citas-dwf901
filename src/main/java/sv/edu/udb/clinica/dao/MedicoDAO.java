package sv.edu.udb.clinica.dao;

import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class MedicoDAO {

    public List<Medico> listarMedicos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Medico buscarPorId(int idMedico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Medico.class, idMedico);
        } finally {
            em.close();
        }
    }

    public boolean insertar(Medico medico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(medico);
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

    public boolean actualizar(Medico medico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(medico);
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

    public boolean eliminar(int idMedico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Medico medico = em.find(Medico.class, idMedico);
            if (medico != null) {
                em.remove(medico);
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

    public List<Especialidad> listarEspecialidades() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Especialidad e ORDER BY e.nombre", Especialidad.class).getResultList();
        } finally {
            em.close();
        }
    }
}