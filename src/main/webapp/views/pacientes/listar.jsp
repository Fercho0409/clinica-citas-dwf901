<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Pacientes</title>
    <!-- Bootstrap para estilos -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Pacientes Registrados</h1>
        
        <!-- Botón para agregar nuevo paciente -->
        <a href="formulario.jsp" class="btn btn-success mb-3"> Nuevo Paciente</a>
        
        <!-- Tabla de pacientes -->
        <table class="table table-striped table-hover">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Email</th>
                    <th>Teléfono</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <!-- Datos de ejemplo (temporales) -->
                <tr>
                    <td>1</td>
                    <td>María</td>
                    <td>González</td>
                    <td>maria@email.com</td>
                    <td>555-1234</td>
                    <td>
                        <a href="editar.jsp" class="btn btn-warning btn-sm"> Editar</a>
                        <button class="btn btn-danger btn-sm"> Eliminar</button>
                    </td>
                </tr>
                <tr>
                    <td>2</td>
                    <td>Carlos</td>
                    <td>Rodríguez</td>
                    <td>carlos@email.com</td>
                    <td>555-5678</td>
                    <td>
                        <a href="editar.jsp" class="btn btn-warning btn-sm"> Editar</a>
                        <button class="btn btn-danger btn-sm"> Eliminar</button>
                    </td>
                </tr>
                <tr>
                    <td>3</td>
                    <td>Ana</td>
                    <td>Martínez</td>
                    <td>ana@email.com</td>
                    <td>555-9012</td>
                    <td>
                        <a href="editar.jsp" class="btn btn-warning btn-sm">✏️ Editar</a>
                        <button class="btn btn-danger btn-sm">🗑️ Eliminar</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>