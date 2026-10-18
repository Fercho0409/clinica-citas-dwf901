package sv.edu.udb.clinica.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import sv.edu.udb.clinica.modelo.Especialidad;

public class EspecialidadDAO {

    private static EntityManagerFactory emf;

    private synchronized static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("clinicaPU");
        }
        return emf;
    }

    public List<Especialidad> obtenerTodas() {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Especialidad e", Especialidad.class).getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Especialidad buscarPorId(Long id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(Especialidad.class, id);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}