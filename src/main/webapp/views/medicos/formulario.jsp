<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty medico ? 'Registrar Nuevo Médico' : 'Editar Médico'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/views/partials/navbar.jsp" %>
    <div class="container mt-5">
        <h1 class="text-center mb-4">
            ${empty medico ? '📋 Registrar Nuevo Médico' : '✏️ Editar Médico'}
        </h1>
        
        <form action="${pageContext.request.contextPath}/medicos" method="POST">
            <!-- Campo oculto indispensable para saber si estamos editando -->
            <input type="hidden" name="idMedico" value="${medico.idMedico}">

            <div class="mb-3">
                <label for="nombres" class="form-label">Nombre:</label>
                <input type="text" class="form-control" id="nombres" name="nombres" value="${medico.nombres}" required>
            </div>

            <div class="mb-3">
                <label for="apellidos" class="form-label">Apellido:</label>
                <input type="text" class="form-control" id="apellidos" name="apellidos" value="${medico.apellidos}" required>
            </div>

            <div class="mb-3">
                <label for="jvpm" class="form-label">DUI:</label>
                <input type="text" class="form-control" id="jvpm" name="jvpm" value="${medico.jvpm}" required>
            </div>

            <div class="mb-3">
                <label for="idEspecialidad" class="form-label">Especialidad (ID):</label>
                <input type="number" class="form-control" id="idEspecialidad" name="idEspecialidad" value="${medico.especialidad.idEspecialidad}" placeholder="Ej: 1, 2, 3..." required>
                <div class="form-text">Ingresa el ID numérico de la especialidad correspondiente.</div>
            </div>

            <div class="mb-3">
                <label for="correo" class="form-label">Email:</label>
                <input type="email" class="form-control" id="correo" name="correo" value="${medico.correo}" required>
            </div>

            <div class="mb-3">
                <label for="telefono" class="form-label">Teléfono:</label>
                <input type="text" class="form-control" id="telefono" name="telefono" value="${medico.telefono}" required>
            </div>

            <button type="submit" class="btn btn-success">💾 Guardar Médico</button>
            <a href="${pageContext.request.contextPath}/medicos?accion=listar" class="btn btn-secondary">❌ Cancelar</a>
        </form>
    </div>
</body>
</html>