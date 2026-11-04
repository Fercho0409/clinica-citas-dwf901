package sv.edu.udb.clinica.api.recurso;

import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import sv.edu.udb.clinica.api.dto.EspecialidadRespuesta;
import sv.edu.udb.clinica.dao.MedicoDAO;

/**
 * Catalogo de especialidades, solo lectura.
 *
 *   GET /api/especialidades
 *
 * Las especialidades se administran desde la base; la API solo las
 * publica para que el cliente arme el formulario de medicos y el
 * filtro del directorio.
 */
@Path("especialidades")
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
public class EspecialidadRecurso {

    private final MedicoDAO medicoDAO = new MedicoDAO();

    @GET
    public List<EspecialidadRespuesta> listar() {
        return medicoDAO.listarEspecialidades().stream()
                .map(EspecialidadRespuesta::desde)
                .collect(Collectors.toList());
    }
}
