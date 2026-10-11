<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Médicos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/views/partials/navbar.jsp" %>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Médicos Registrados</h1>
        
        <!-- Botón para agregar nuevo médico pasando por el Servlet -->
        <a href="${pageContext.request.contextPath}/medicos?accion=nuevo" class="btn btn-success mb-3"> Nuevo Médico</a>
        
        <!-- Tabla de médicos -->
        <table class="table table-striped table-hover">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>DUI / JVPM</th>
                    <th>Especialidad</th>
                    <th>Email</th>
                    <th>Teléfono</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="medico" items="${listaMedicos}">
                    <tr>
                        <td>${medico.idMedico}</td>
                        <td>${medico.nombres}</td>
                        <td>${medico.apellidos}</td>
                        <td>${medico.jvpm}</td>
                        <td>${medico.especialidad.nombre}</td>
                        <td>${medico.correo}</td>
                        <td>${medico.telefono}</td>
                        <td>
                            <!-- Enlace para editar pasando el ID y la acción al Servlet -->
                            <a href="${pageContext.request.contextPath}/medicos?accion=editar&id=${medico.idMedico}" class="btn btn-warning btn-sm">✏️ Editar</a>
                            
                            <!-- Enlace para eliminar pasando el ID y la acción al Servlet -->
                            <a href="${pageContext.request.contextPath}/medicos?accion=eliminar&id=${medico.idMedico}" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro de eliminar este médico?');">🗑️ Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
                
                <c:if test="${empty listaMedicos}">
                    <tr>
                        <td colspan="8" class="text-center">No hay médicos registrados.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>