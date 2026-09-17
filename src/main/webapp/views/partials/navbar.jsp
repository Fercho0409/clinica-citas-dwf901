<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 shadow-sm">
    <div class="container">
        <!-- Logo / Título que te lleva al Inicio -->
        <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">🏥 Clínica UDB</a>
        
        <!-- Botones alineados a la derecha -->
        <div class="ms-auto d-flex gap-2">
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-outline-light btn-sm">
                🏠 Inicio
            </a>
            <a href="javascript:history.back()" class="btn btn-outline-secondary btn-sm text-white">
                ⬅️ Volver
            </a>
        </div>
    </div>
</nav>