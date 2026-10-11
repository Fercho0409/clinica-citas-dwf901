<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema de Gestión de Citas</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <div class="container mt-5">
        <!-- Encabezado Principal -->
        <div class="p-5 mb-4 bg-white rounded-3 shadow-sm border">
            <div class="container-fluid py-3">
                <h1 class="display-5 fw-bold text-primary">🏥 Sistema de Gestión de Citas</h1>
                <p class="col-md-8 fs-4 text-muted">Proyecto DWF901 - Fase 1 | Módulo Administrativo</p>
                <hr class="my-4">
                <p class="fs-6">Selecciona una opción del menú para comenzar a administrar el sistema:</p>
                
                <!-- Botones de Navegación Estilizados -->
                <div class="d-flex gap-3 flex-wrap">
                    <a href="${pageContext.request.contextPath}/medicos?accion=listar" class="btn btn-primary btn-lg">
                        👨‍⚕️ Gestión de Médicos
                    </a>
                    <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="btn btn-outline-secondary btn-lg">
                        👥 Gestión de Pacientes
                    </a>
                    <a href="${pageContext.request.contextPath}/prueba-conexion" class="btn btn-outline-info btn-lg">
                        🔌 Probar Conexión BD
                    </a>
                </div>
            </div>
        </div>
    </div>

</body>
</html>