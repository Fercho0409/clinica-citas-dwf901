<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Paciente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h2 class="text-center mb-4">Editar Paciente</h2>
                
                <!-- Formulario de edición -->
                <form action="PacienteServlet" method="POST">
                    <!-- Campo oculto para indicar la acción -->
                    <input type="hidden" name="accion" value="actualizar">
                    
                    <!-- ID del paciente (temporal, luego será dinámico) -->
                    <input type="hidden" name="id" value="1">
                    
                    <div class="mb-3">
                        <label for="nombre" class="form-label">Nombre:</label>
                        <input type="text" class="form-control" id="nombre" 
                               name="nombre" value="María" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="apellido" class="form-label">Apellido:</label>
                        <input type="text" class="form-control" id="apellido" 
                               name="apellido" value="González" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" class="form-control" id="email" 
                               name="email" value="maria@email.com" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="telefono" class="form-label">Teléfono:</label>
                        <input type="tel" class="form-control" id="telefono" 
                               name="telefono" value="555-1234">
                    </div>
                    
                    <div class="text-center mt-4">
                        <button type="submit" class="btn btn-primary"> Actualizar</button>
                        <a href="listar.jsp" class="btn btn-secondary"> Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>