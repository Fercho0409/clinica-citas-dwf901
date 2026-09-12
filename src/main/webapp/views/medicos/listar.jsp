<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Médicos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4"> Médicos Registrados</h1>
        
        <!-- Botón para agregar nuevo médico -->
        <a href="formulario.jsp" class="btn btn-success mb-3"> Nuevo Médico</a>
        
        <!-- Tabla de médicos -->
        <table class="table table-striped table-hover">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Especialidad</th>
                    <th>Email</th>
                    <th>Teléfono</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <!-- Datos de ejemplo (temporales) -->
                <tr>
                    <td>1</td>
                    <td>Roberto</td>
                    <td>Silva</td>
                    <td>Cardiología</td>
                    <td>roberto@clinica.com</td>
                    <td>555-1001</td>
                    <td>
                        <a href="editar.jsp" class="btn btn-warning btn-sm">✏️ Editar</a>
                        <button class="btn btn-danger btn-sm">🗑️ Eliminar</button>
                    </td>
                </tr>
                <tr>
                    <td>2</td>
                    <td>Patricia</td>
                    <td>López</td>
                    <td>Pediatría</td>
                    <td>patricia@clinica.com</td>
                    <td>555-1002</td>
                    <td>
                        <a href="editar.jsp" class="btn btn-warning btn-sm">✏️ Editar</a>
                        <button class="btn btn-danger btn-sm">🗑️ Eliminar</button>
                    </td>
                </tr>
                <tr>
                    <td>3</td>
                    <td>Fernando</td>
                    <td>Ramírez</td>
                    <td>Dermatología</td>
                    <td>fernando@clinica.com</td>
                    <td>555-1003</td>
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