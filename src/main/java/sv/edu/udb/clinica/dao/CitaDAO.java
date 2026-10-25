package sv.edu.udb.clinica.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityManager;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;
import sv.edu.udb.clinica.util.JPAUtil;

/**
 * Acceso a datos de citas, implementado con JPA.
 *
 * Aqui vive la consulta que sostiene la regla de negocio principal:
 * un medico no puede tener dos citas vigentes a la misma hora.
 */
public class CitaDAO {

    /**
     * JOIN FETCH trae paciente, medico y especialidad en una sola
     * consulta. Sin esto, listar N citas dispararia una consulta
     * adicional por cada relacion de cada fila.
     */
    public List<Cita> listarCitas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Cita c "
                    + "JOIN FETCH c.paciente "
                    + "JOIN FETCH c.medico m "
                    + "JOIN FETCH m.especialidad "
                    + "ORDER BY c.fecha DESC, c.hora DESC",
                    Cita.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Cita buscarPorId(Integer idCita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Cita.class, idCita);
        } finally {
            em.close();
        }
    }

    /**
     * Agenda del dia de un medico (HU-14).
     * Todas las citas del medico ese dia, ordenadas por hora, cualquiera sea su estado
     */
    public List<Cita> agendaDelDia(Integer idMedico, LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Cita c "
                    + "JOIN FETCH c.paciente "
                    + "JOIN FETCH c.medico "
                    + "WHERE c.medico.idMedico = :idMedico "
                    + "AND c.fecha = :fecha "
                    + "ORDER BY c.hora",
                    Cita.class)
                    .setParameter("idMedico", idMedico)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Regla HU-12: indica si el medico ya tiene una cita vigente en esa
     * fecha y hora.
     *
     * Solo cuentan los estados PROGRAMADA y REPROGRAMADA: si una cita
     * fue cancelada, su horario vuelve a quedar libre.
     *
     * idCitaExcluir permite reprogramar una cita sin que choque consigo
     * misma. Para una cita nueva se pasa null.
     */
    public boolean medicoOcupado(Integer idMedico, LocalDate fecha,
            LocalTime hora, Integer idCitaExcluir) {

        int excluir = (idCitaExcluir != null) ? idCitaExcluir : 0;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long cantidad = em.createQuery(
                    "SELECT COUNT(c) FROM Cita c "
                    + "WHERE c.medico.idMedico = :idMedico "
                    + "AND c.fecha = :fecha "
                    + "AND c.hora = :hora "
                    + "AND c.idCita <> :idExcluir "
                    + "AND c.estado IN (:programada, :reprogramada)", Long.class)
                    .setParameter("idMedico", idMedico)
                    .setParameter("fecha", fecha)
                    .setParameter("hora", hora)
                    .setParameter("idExcluir", excluir)
                    .setParameter("programada", EstadoCita.PROGRAMADA)
                    .setParameter("reprogramada", EstadoCita.REPROGRAMADA)
                    .getSingleResult();
            return cantidad > 0;
        } finally {
            em.close();
        }
    }

    public boolean insertar(Cita cita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Se obtienen las referencias administradas de paciente y
            // medico antes de guardar, para que JPA no intente
            // insertarlos de nuevo
            cita.setPaciente(em.find(Paciente.class,
                    cita.getPaciente().getIdPaciente()));
            cita.setMedico(em.find(Medico.class,
                    cita.getMedico().getIdMedico()));
            em.persist(cita);
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

    public boolean actualizar(Cita cita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            cita.setPaciente(em.find(Paciente.class,
                    cita.getPaciente().getIdPaciente()));
            cita.setMedico(em.find(Medico.class,
                    cita.getMedico().getIdMedico()));
            em.merge(cita);
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

    /**
     * Cambia solo el estado de la cita, sin tocar los demas datos.
     * Lo usan los botones de cancelar y marcar como atendida.
     */
    public boolean cambiarEstado(Integer idCita, EstadoCita nuevoEstado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, idCita);
            if (cita != null) {
                cita.setEstado(nuevoEstado);
            }
            em.getTransaction().commit();
            return cita != null;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(Integer idCita) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cita cita = em.find(Cita.class, idCita);
            if (cita != null) {
                em.remove(cita);
            }
            em.getTransaction().commit();
            return cita != null;
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
