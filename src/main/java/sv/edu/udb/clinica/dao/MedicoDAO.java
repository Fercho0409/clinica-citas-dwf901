package sv.edu.udb.clinica.dao;

import java.util.List;
import javax.persistence.EntityManager;
import sv.edu.udb.clinica.modelo.Especialidad;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.util.JPAUtil;

/**
 * Acceso a datos de medicos, implementado con JPA.
 *
 * El JOIN con especialidades ya no se escribe a mano: la relacion
 * ManyToOne de la entidad hace que Hibernate traiga la especialidad
 * junto con el medico.
 */
public class MedicoDAO {

    public List<Medico> listarMedicos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT m FROM Medico m JOIN FETCH m.especialidad "
                    + "ORDER BY m.apellidos, m.nombres",
                    Medico.class).getResultList();
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

    /**
     * Indica si ya existe otro medico con ese DUI.
     * idExcluir permite editar sin que choque consigo mismo;
     * para un registro nuevo se pasa null.
     */
    public boolean existeDui(String dui, Integer idExcluir) {
        int excluir = (idExcluir != null) ? idExcluir : 0;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long cantidad = em.createQuery(
                    "SELECT COUNT(m) FROM Medico m "
                    + "WHERE m.dui = :dui AND m.idMedico <> :id", Long.class)
                    .setParameter("dui", dui)
                    .setParameter("id", excluir)
                    .getSingleResult();
            return cantidad > 0;
        } finally {
            em.close();
        }
    }

    public List<Especialidad> listarEspecialidades() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Especialidad e ORDER BY e.nombre",
                    Especialidad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean insertar(Medico medico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // La especialidad ya existe en la base: se toma su
            // referencia administrada antes de guardar el medico
            if (medico.getEspecialidad() != null) {
                medico.setEspecialidad(em.find(Especialidad.class,
                        medico.getEspecialidad().getIdEspecialidad()));
            }
            em.persist(medico);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean actualizar(Medico medico) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (medico.getEspecialidad() != null) {
                medico.setEspecialidad(em.find(Especialidad.class,
                        medico.getEspecialidad().getIdEspecialidad()));
            }
            em.merge(medico);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
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
            }
            em.getTransaction().commit();
            return medico != null;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
