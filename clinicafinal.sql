-- ============================================================
-- BASE DE DATOS: CLINICA
-- Integrante 4 - Base de datos
-- ============================================================
-- Este script crea la base de datos desde cero e inserta
-- los datos de prueba requeridos:
--   5 especialidades
--   10 pacientes
--   5 médicos
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
-- VERIFICACIÓN
-- ============================================================

SELECT 'especialidades' AS tabla, COUNT(*) AS registros FROM especialidades
UNION ALL
SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL
SELECT 'medicos', COUNT(*) FROM medicos;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
USE clinica;

SELECT COUNT(*) AS total_especialidades FROM especialidades;
SELECT COUNT(*) AS total_pacientes FROM pacientes;
SELECT COUNT(*) AS total_medicos FROM medicos;
USE clinica;

SELECT 
    m.id_medico,
    CONCAT(m.nombres, ' ', m.apellidos) AS medico,
    e.nombre AS especialidad
FROM medicos m
INNER JOIN especialidades e 
    ON m.id_especialidad = e.id_especialidad;
    USE clinica;

SELECT * FROM pacientes;

SELECT * FROM medicos;

SELECT * FROM especialidades;