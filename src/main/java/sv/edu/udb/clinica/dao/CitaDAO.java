package sv.edu.udb.clinica.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.clinica.modelo.Cita;
import sv.edu.udb.clinica.modelo.EstadoCita;
import sv.edu.udb.clinica.modelo.Medico;
import sv.edu.udb.clinica.modelo.Paciente;

/**
 * Acceso a datos de citas, administrado por Spring.
 *
 * Aqui vive la consulta que sostiene la regla de negocio principal:
 * un medico no puede tener dos citas vigentes a la misma hora.
 *
 * ===================================================================
 *  Que cambio en la Fase 4
 * ===================================================================
 *
 * Antes cada metodo hacia lo mismo: pedirle una conexion a JPAUtil,
 * abrir una transaccion si iba a escribir, confirmarla, deshacerla en
 * caso de error y cerrar la conexion en un finally. Eso son seis
 * lineas de plomeria repetidas en cada metodo, y basta olvidar un
 * em.close() para ir dejando conexiones abiertas hasta agotar la base.
 *
 * Ahora Spring se encarga de las dos cosas:
 *
 *   @Repository          le dice a Spring que administre esta clase.
 *                        En lugar de hacer "new CitaDAO()", quien la
 *                        necesite la recibe ya construida.
 *
 *   @PersistenceContext  Spring inyecta la conexion. No hay que
 *                        pedirla ni cerrarla: la abre antes de cada
 *                        operacion y la cierra despues, siempre, aunque
 *                        el metodo falle.
 *
 *   @Transactional       marca los metodos que escriben. Spring abre la
 *                        transaccion al entrar, la confirma al salir
 *                        bien y la deshace entera si se lanza una
 *                        excepcion.
 *
 * El resultado es el mismo comportamiento con la mitad del codigo, y
 * sin posibilidad de olvidar un cierre.
 *
 * Nota sobre el nombre: "DAO" y "repositorio" son dos nombres para el
 * mismo patron, el objeto que aisla el acceso a datos del resto de la
 * aplicacion. Se conservo el nombre DAO para no romper las referencias
 * del resto del proyecto; la anotacion @Repository es la que importa.
 */
@Repository
public class CitaDAO {

    /**
     * La conexion con la base. La pone Spring, y es la misma que esta
     * usando la transaccion en curso, de modo que todo lo que se haga
     * en un metodo @Transactional viaja junto.
     */
    @PersistenceContext
    private EntityManager em;

    // ==================== Consultas ====================

    /**
     * JOIN FETCH trae paciente, medico y especialidad en una sola
     * consulta. Sin esto, listar N citas dispararia una consulta
     * adicional por cada relacion de cada fila.
     */
    public List<Cita> listarCitas() {
        return em.createQuery(
                "SELECT c FROM Cita c "
                + "JOIN FETCH c.paciente "
                + "JOIN FETCH c.medico m "
                + "JOIN FETCH m.especialidad "
                + "ORDER BY c.fecha DESC, c.hora DESC",
                Cita.class).getResultList();
    }

    public Cita buscarPorId(Integer idCita) {
        return em.find(Cita.class, idCita);
    }

    /**
     * Agenda del dia de un medico (HU-14).
     * Todas las citas del medico ese dia, ordenadas por hora,
     * cualquiera sea su estado: el medico necesita ver tanto lo que va
     * a atender como lo que se cancelo.
     */
    public List<Cita> agendaDelDia(Integer idMedico, LocalDate fecha) {
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
    }

    // ==================== Escritura ====================

    @Transactional
    public boolean insertar(Cita cita) {
        // Se obtienen las referencias administradas de paciente y
        // medico antes de guardar, para que JPA no intente
        // insertarlos de nuevo
        cita.setPaciente(em.find(Paciente.class,
                cita.getPaciente().getIdPaciente()));
        cita.setMedico(em.find(Medico.class,
                cita.getMedico().getIdMedico()));
        em.persist(cita);
        return true;
    }

    @Transactional
    public boolean actualizar(Cita cita) {
        cita.setPaciente(em.find(Paciente.class,
                cita.getPaciente().getIdPaciente()));
        cita.setMedico(em.find(Medico.class,
                cita.getMedico().getIdMedico()));
        em.merge(cita);
        return true;
    }

    /**
     * Cambia solo el estado de la cita, sin tocar los demas datos.
     * Lo usan los botones de cancelar y marcar como atendida.
     *
     * No hace falta llamar a merge ni a nada parecido: dentro de una
     * transaccion, JPA vigila los objetos que cargo y, al confirmar,
     * escribe solo lo que cambio.
     */
    @Transactional
    public boolean cambiarEstado(Integer idCita, EstadoCita nuevoEstado) {
        Cita cita = em.find(Cita.class, idCita);
        if (cita != null) {
            cita.setEstado(nuevoEstado);
        }
        return cita != null;
    }

    @Transactional
    public boolean eliminar(Integer idCita) {
        Cita cita = em.find(Cita.class, idCita);
        if (cita != null) {
            em.remove(cita);
        }
        return cita != null;
    }
}