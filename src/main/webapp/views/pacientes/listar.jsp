<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Pacientes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Pacientes Registrados</h1>

        <a href="${pageContext.request.contextPath}/pacientes?accion=nuevo"
           class="btn btn-success mb-3">Nuevo Paciente</a>

        <table class="table table-striped table-hover">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombres</th>
                    <th>Apellidos</th>
                    <th>DUI</th>
                    <th>Fecha de nacimiento</th>
                    <th>Teléfono</th>
                    <th>Correo</th>
                    <th>Dirección</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="paciente" items="${listaPacientes}">
                    <tr>
                        <td>${paciente.idPaciente}</td>
                        <td><c:out value="${paciente.nombres}"/></td>
                        <td><c:out value="${paciente.apellidos}"/></td>
                        <td><c:out value="${paciente.dui}"/></td>
                        <td>${paciente.fechaNacimiento}</td>
                        <td><c:out value="${paciente.telefono}"/></td>
                        <td><c:out value="${paciente.correo}"/></td>
                        <td><c:out value="${paciente.direccion}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/pacientes?accion=editar&id=${paciente.idPaciente}"
                               class="btn btn-warning btn-sm">Editar</a>
                            <a href="${pageContext.request.contextPath}/pacientes?accion=eliminar&id=${paciente.idPaciente}"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Eliminar este paciente?');">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty listaPacientes}">
                    <tr>
                        <td colspan="9" class="text-center text-muted">
                            No hay pacientes registrados.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>