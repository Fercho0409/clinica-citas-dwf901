// Cargar scripts de AdminLTE de forma ordenada
(function() {
    function cargar(src, next) {
        var s = document.createElement('script');
        s.src = src;
        s.onload = next;
        document.body.appendChild(s);
    }
    cargar('https://code.jquery.com/jquery-3.7.1.min.js', function() {
        cargar('https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js', function() {
            cargar('https://cdn.jsdelivr.net/npm/admin-lte@3.2/dist/js/adminlte.min.js');
        });
    });
})();