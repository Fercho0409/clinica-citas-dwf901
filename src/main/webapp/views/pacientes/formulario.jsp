<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty paciente ? "Registrar Paciente" : "Modificar Paciente"}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/views/partials/navbar.jsp" %>
    <div class="container mt-5 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-7">
                <div class="card shadow">
                    <div class="card-header bg-dark text-white text-center">
                        <h2>${empty paciente ? "📋 Registrar Nuevo Paciente" : "✏️ Modificar Paciente"}</h2>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/pacientes" method="POST">
                            
                            <!-- Campo oculto para el ID (permite al servlet saber si es inserción o actualización) -->
                            <input type="hidden" name="idPaciente" value="${paciente.idPaciente}">
                            
                            <div class="mb-3">
                                <label for="nombres" class="form-label">Nombres:</label>
                                <input type="text" class="form-control" id="nombres" name="nombres" value="${paciente.nombres}" required placeholder="Ej: Juan Antonio">
                            </div>
                            
                            <div class="mb-3">
                                <label for="apellidos" class="form-label">Apellidos:</label>
                                <input type="text" class="form-control" id="apellidos" name="apellidos" value="${paciente.apellidos}" required placeholder="Ej: Pérez Gómez">
                            </div>

                            <div class="mb-3">
                                <label for="dui" class="form-label">DUI:</label>
                                <input type="text" class="form-control" id="dui" name="dui" value="${paciente.dui}" placeholder="Ej: 05555555-5">
                            </div>

                            <div class="mb-3">
                                <label for="fechaNacimiento" class="form-label">Fecha de Nacimiento:</label>
                                <input type="date" class="form-control" id="fechaNacimiento" name="fechaNacimiento" value="${paciente.fechaNacimiento}">
                            </div>
                            
                            <div class="mb-3">
                                <label for="telefono" class="form-label">Teléfono:</label>
                                <input type="text" class="form-control" id="telefono" name="telefono" value="${paciente.telefono}" placeholder="Ej: 7000-0000">
                            </div>

                            <div class="mb-3">
                                <label for="correo" class="form-label">Correo Electrónico:</label>
                                <input type="email" class="form-control" id="correo" name="correo" value="${paciente.correo}" placeholder="Ej: juan@email.com">
                            </div>

                            <div class="mb-3">
                                <label for="direccion" class="form-label">Dirección:</label>
                                <textarea class="form-control" id="direccion" name="direccion" rows="2" placeholder="Ej: San Salvador">${paciente.direccion}</textarea>
                            </div>
                            
                            <div class="text-center mt-4">
                                <button type="submit" class="btn btn-success">💾 Guardar Paciente</button>
                                <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="btn btn-secondary">❌ Cancelar</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>