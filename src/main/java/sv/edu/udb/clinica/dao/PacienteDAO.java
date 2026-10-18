package sv.edu.udb.clinica.dao;

import java.util.List;
import javax.persistence.EntityManager;
import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.util.JPAUtil;

/**
 * Acceso a datos de pacientes, implementado con JPA.
 *
 * Las operaciones de escritura propagan la excepcion en lugar de
 * capturarla en silencio: asi el controlador puede explicarle al
 * usuario que fue lo que fallo.
 */
public class PacienteDAO {

    public List<Paciente> listarPacientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p ORDER BY p.apellidos, p.nombres",
                    Paciente.class).getResultList();
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

    /**
     * Indica si ya existe otro paciente con ese DUI.
     *
     * Se consulta antes de guardar en lugar de esperar a que la base
     * rechace la operacion por la restriccion UNIQUE, de modo que el
     * mensaje al usuario sea claro.
     *
     * idExcluir permite editar un paciente sin que choque consigo mismo;
     * para un registro nuevo se pasa null.
     */
    public boolean existeDui(String dui, Integer idExcluir) {
        int excluir = (idExcluir != null) ? idExcluir : 0;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long cantidad = em.createQuery(
                    "SELECT COUNT(p) FROM Paciente p "
                    + "WHERE p.dui = :dui AND p.idPaciente <> :id", Long.class)
                    .setParameter("dui", dui)
                    .setParameter("id", excluir)
                    .getSingleResult();
            return cantidad > 0;
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
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
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
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
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
            }
            em.getTransaction().commit();
            return paciente != null;
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
