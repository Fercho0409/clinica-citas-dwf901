-- ============================================================
-- BASE DE DATOS: CLINICA - FASE 2
-- Integrante 3 - Base de datos, citas y PDF de evidencias
-- ============================================================
-- Script basado en el de la Fase 1 (clinicafinal.sql).
-- Crea la base de datos desde cero e inserta datos de prueba:
--   5 especialidades
--   10 pacientes
--   5 médicos
--   12 citas (NUEVO en Fase 2)
--
-- Cambios respecto a la Fase 1:
--   * Nueva tabla "citas" con relaciones hacia pacientes y medicos.
--   * Datos de prueba de citas en distintos estados.
--   * Consultas de verificación ordenadas al final del script.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS clinica;
CREATE DATABASE clinica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE clinica;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- TABLA: especialidades
-- ============================================================

CREATE TABLE especialidades (
    id_especialidad INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id_especialidad)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- TABLA: pacientes
-- ============================================================

CREATE TABLE pacientes (
    id_paciente INT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dui VARCHAR(10) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    correo VARCHAR(100) DEFAULT NULL,
    direccion VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (id_paciente),
    UNIQUE KEY uk_pacientes_dui (dui)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- TABLA: medicos
-- ============================================================

CREATE TABLE medicos (
    id_medico INT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dui VARCHAR(10) NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    correo VARCHAR(100) DEFAULT NULL,
    id_especialidad INT NOT NULL,
    PRIMARY KEY (id_medico),
    UNIQUE KEY uk_medicos_dui (dui),
    KEY idx_medico_especialidad (id_especialidad),
    CONSTRAINT fk_medico_especialidad
        FOREIGN KEY (id_especialidad)
        REFERENCES especialidades (id_especialidad)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- TABLA: citas  (NUEVA - FASE 2)
-- ------------------------------------------------------------
-- estado:
--   PROGRAMADA   -> cita activa, pendiente de atender
--   REPROGRAMADA -> se le cambió la fecha u hora
--   CANCELADA    -> ya no se atenderá (libera el horario)
--   ATENDIDA     -> el paciente ya pasó consulta
--
-- Nota: NO se usa UNIQUE (id_medico, fecha, hora) porque una
-- cita CANCELADA debe liberar ese horario. La regla de "un
-- médico no puede tener dos citas a la misma hora" y la de
-- "no agendar en fechas pasadas" se validan en la aplicación.
-- ============================================================

CREATE TABLE citas (
    id_cita INT NOT NULL AUTO_INCREMENT,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    motivo VARCHAR(255) DEFAULT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_cita),
    KEY idx_cita_paciente (id_paciente),
    KEY idx_cita_medico_fecha_hora (id_medico, fecha, hora),
    CONSTRAINT fk_cita_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES pacientes (id_paciente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_cita_medico
        FOREIGN KEY (id_medico)
        REFERENCES medicos (id_medico)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_cita_estado
        CHECK (estado IN ('PROGRAMADA', 'REPROGRAMADA', 'CANCELADA', 'ATENDIDA'))
) ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- DATOS DE PRUEBA: 5 ESPECIALIDADES
-- ============================================================

INSERT INTO especialidades (nombre, descripcion) VALUES
('Medicina General', 'Atención médica general y preventiva'),
('Pediatría', 'Atención médica especializada para niños'),
('Cardiología', 'Diagnóstico y tratamiento de enfermedades cardiovasculares'),
('Dermatología', 'Diagnóstico y tratamiento de enfermedades de la piel'),
('Ginecología', 'Atención médica especializada en salud femenina');

-- ============================================================
-- DATOS DE PRUEBA: 10 PACIENTES
-- ============================================================

INSERT INTO pacientes
(nombres, apellidos, dui, fecha_nacimiento, telefono, correo, direccion)
VALUES
('Carlos', 'Martínez', '01234567-8', '1995-03-15', '7012-3456', 'carlos.martinez@gmail.com', 'San Salvador'),
('Ana', 'Gómez', '12345678-9', '1998-07-22', '7123-4567', 'ana.gomez@gmail.com', 'Santa Tecla'),
('José', 'Hernández', '23456789-0', '1987-11-08', '7234-5678', 'jose.hernandez@gmail.com', 'Soyapango'),
('María', 'López', '34567890-1', '2001-01-30', '7345-6789', 'maria.lopez@gmail.com', 'Antiguo Cuscatlán'),
('Luis', 'Ramírez', '45678901-2', '1992-05-17', '7456-7890', 'luis.ramirez@gmail.com', 'San Marcos'),
('Sofía', 'Castro', '56789012-3', '1999-09-25', '7567-8901', 'sofia.castro@gmail.com', 'Mejicanos'),
('Miguel', 'Flores', '67890123-4', '1985-12-12', '7678-9012', 'miguel.flores@gmail.com', 'Apopa'),
('Laura', 'Mendoza', '78901234-5', '1996-04-05', '7789-0123', 'laura.mendoza@gmail.com', 'Ilopango'),
('Daniel', 'Rivera', '89012345-6', '2000-08-19', '7890-1234', 'daniel.rivera@gmail.com', 'Colón'),
('Gabriela', 'Morales', '90123456-7', '1993-10-27', '7901-2345', 'gabriela.morales@gmail.com', 'San Miguel');

-- ============================================================
-- DATOS DE PRUEBA: 5 MÉDICOS
-- ============================================================

INSERT INTO medicos
(nombres, apellidos, dui, telefono, correo, id_especialidad)
VALUES
('Roberto', 'Martínez', '11223344-5', '7011-2233', 'roberto.martinez@clinica.com', 1),
('Patricia', 'Gómez', '22334455-6', '7022-3344', 'patricia.gomez@clinica.com', 2),
('Fernando', 'Hernández', '33445566-7', '7033-4455', 'fernando.hernandez@clinica.com', 3),
('Claudia', 'López', '44556677-8', '7044-5566', 'claudia.lopez@clinica.com', 4),
('Ricardo', 'Ramírez', '55667788-9', '7055-6677', 'ricardo.ramirez@clinica.com', 5);

-- ============================================================
-- DATOS DE PRUEBA: 12 CITAS (NUEVO - FASE 2)
-- ------------------------------------------------------------
-- Las fechas se calculan a partir del día en que se corre el
-- script (CURDATE()), así las citas "futuras" siempre son
-- futuras y las "pasadas" siempre son pasadas, sin importar
-- el día de la defensa.
-- ============================================================

INSERT INTO citas (id_paciente, id_medico, fecha, hora, estado, motivo) VALUES
-- Citas pasadas (historial)
(1, 1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), '08:00:00', 'ATENDIDA',     'Chequeo general anual'),
(2, 2, DATE_SUB(CURDATE(), INTERVAL 7 DAY),  '09:30:00', 'ATENDIDA',     'Control pediátrico'),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY),  '10:00:00', 'CANCELADA',    'Dolor en el pecho - paciente canceló'),

-- Citas futuras programadas
(4, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY),  '08:30:00', 'PROGRAMADA',   'Revisión de manchas en la piel'),
(5, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '09:00:00', 'PROGRAMADA',   'Fiebre y malestar general'),
(6, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '10:30:00', 'PROGRAMADA',   'Control ginecológico'),
(7, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  '11:00:00', 'PROGRAMADA',   'Evaluación cardiovascular'),
(8, 2, DATE_ADD(CURDATE(), INTERVAL 4 DAY),  '14:00:00', 'PROGRAMADA',   'Vacunación'),

-- Cita reprogramada
(9, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  '15:00:00', 'REPROGRAMADA', 'Seguimiento - se movió por solicitud del paciente'),

-- Cita futura cancelada: su horario queda libre
(10, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00:00', 'CANCELADA',    'Consulta dermatológica - cancelada'),

-- Mismo médico (1) en otra hora del mismo día de la cita 5 (válido)
(2, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  '11:00:00', 'PROGRAMADA',   'Dolor de cabeza frecuente'),

-- Ocupa el mismo horario que la cita cancelada (id 10): es válido
-- porque la cancelada ya no cuenta
(1, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  '09:00:00', 'PROGRAMADA',   'Alergia en la piel');

-- ============================================================
-- VERIFICACIÓN
-- ============================================================

-- Conteo de registros por tabla
SELECT 'especialidades' AS tabla, COUNT(*) AS registros FROM especialidades
UNION ALL
SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL
SELECT 'medicos', COUNT(*) FROM medicos
UNION ALL
SELECT 'citas', COUNT(*) FROM citas;

-- Médicos con su especialidad
SELECT
    m.id_medico,
    CONCAT(m.nombres, ' ', m.apellidos) AS medico,
    e.nombre AS especialidad
FROM medicos m
INNER JOIN especialidades e
    ON m.id_especialidad = e.id_especialidad;

-- Citas con nombre de paciente, médico y especialidad
SELECT
    c.id_cita,
    c.fecha,
    c.hora,
    CONCAT(p.nombres, ' ', p.apellidos) AS paciente,
    CONCAT(m.nombres, ' ', m.apellidos) AS medico,
    e.nombre AS especialidad,
    c.estado,
    c.motivo
FROM citas c
INNER JOIN pacientes p      ON c.id_paciente = p.id_paciente
INNER JOIN medicos m        ON c.id_medico = m.id_medico
INNER JOIN especialidades e ON m.id_especialidad = e.id_especialidad
ORDER BY c.fecha, c.hora;

-- Estructura de la tabla nueva (útil para la captura del PDF)
DESCRIBE citas;

-- ============================================================
-- PRUEBAS OPCIONALES (correr una por una para las capturas)
-- Están comentadas para que el script completo no falle.
-- ============================================================

-- 1) Llave foránea: paciente que no existe -> debe dar ERROR 1452
-- INSERT INTO citas (id_paciente, id_medico, fecha, hora, motivo)
-- VALUES (999, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', 'Prueba FK');

-- 2) Estado no permitido -> debe dar ERROR 3819 (CHECK)
-- INSERT INTO citas (id_paciente, id_medico, fecha, hora, estado, motivo)
-- VALUES (1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00', 'PERDIDA', 'Prueba CHECK');

-- 3) Borrar un paciente que tiene citas -> debe dar ERROR 1451
-- DELETE FROM pacientes WHERE id_paciente = 1;

-- 4) Consulta que usará la aplicación para detectar choque de horario
--    (si devuelve > 0, el médico ya está ocupado a esa hora)
-- SELECT COUNT(*) AS ocupado
-- FROM citas
-- WHERE id_medico = 1
--   AND fecha = DATE_ADD(CURDATE(), INTERVAL 2 DAY)
--   AND hora = '09:00:00'
--   AND estado <> 'CANCELADA';

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
