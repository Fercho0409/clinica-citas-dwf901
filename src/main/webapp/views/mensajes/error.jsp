<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="alert alert-danger text-center" role="alert">
                    <h2>❌ ¡Ha ocurrido un error!</h2>
                    <p class="lead">No se pudo completar la operación.</p>
                    <p>Por favor intenta nuevamente.</p>
                    <a href="../pacientes/listar.jsp" class="btn btn-danger mt-3">
                         Volver a la Lista
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>