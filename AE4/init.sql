-- Configuración UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS fichajes
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE fichajes;

-- Tabla de trabajadores
CREATE TABLE IF NOT EXISTS trabajadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    password VARCHAR(255) NOT NULL DEFAULT '1234',
    fecha_alta DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de fichajes
CREATE TABLE IF NOT EXISTS fichajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trabajador_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
    temperatura DECIMAL(5,2),
    descripcion_clima VARCHAR(100),
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE,
    INDEX idx_trabajador_fecha (trabajador_id, fecha),
    INDEX idx_fecha (fecha)
);

-- -----------------------------
-- NUEVOS TRABAJADORES
-- -----------------------------
INSERT INTO trabajadores (nombre, apellidos, dni, email, telefono, password, fecha_alta) VALUES
('Laura', 'Méndez Rubio', '90112233A', 'laura.mendez@empresa.com', '600111222', '1234', '2024-01-10'),
('Javier', 'Santos Prieto', '11229944B', 'javier.santos@empresa.com', '600222333', '1234', '2024-01-18'),
('Elena', 'Cano Villar', '77889911C', 'elena.cano@empresa.com', '600333444', '1234', '2024-02-05'),
('Diego', 'Fuentes Marín', '66554433D', 'diego.fuentes@empresa.com', '600444555', '1234', '2024-03-12');

-- -----------------------------
-- NUEVOS FICHAJES
-- -----------------------------

-- Trabajador 1 (Laura)
INSERT INTO fichajes (trabajador_id, fecha, hora, tipo, temperatura, descripcion_clima) VALUES
(1, '2025-12-01', '07:50:00', 'ENTRADA', 11.2, 'Nublado'),
(1, '2025-12-01', '15:05:00', 'SALIDA', 14.8, 'Parcialmente nublado'),
(1, '2025-12-02', '07:53:00', 'ENTRADA', 10.9, 'Lluvioso'),
(1, '2025-12-02', '14:58:00', 'SALIDA', 13.5, 'Lluvioso');

-- Trabajador 2 (Javier)
INSERT INTO fichajes (trabajador_id, fecha, hora, tipo, temperatura, descripcion_clima) VALUES
(2, '2025-12-01', '08:10:00', 'ENTRADA', 12.0, 'Despejado'),
(2, '2025-12-01', '16:20:00', 'SALIDA', 15.3, 'Despejado'),
(2, '2025-12-02', '08:12:00', 'ENTRADA', 11.1, 'Nublado'),
(2, '2025-12-02', '16:05:00', 'SALIDA', 13.7, 'Nublado');

-- Trabajador 3 (Elena)
INSERT INTO fichajes (trabajador_id, fecha, hora, tipo, temperatura, descripcion_clima) VALUES
(3, '2025-12-01', '09:00:00', 'ENTRADA', 12.8, 'Parcialmente nublado'),
(3, '2025-12-01', '17:00:00', 'SALIDA', 15.1, 'Despejado'),
(3, '2025-12-02', '08:55:00', 'ENTRADA', 10.5, 'Lluvioso'),
(3, '2025-12-02', '16:50:00', 'SALIDA', 12.9, 'Lluvioso');

-- Trabajador 4 (Diego)
INSERT INTO fichajes (trabajador_id, fecha, hora, tipo, temperatura, descripcion_clima) VALUES
(4, '2025-12-01', '07:40:00', 'ENTRADA', 10.7, 'Nublado'),
(4, '2025-12-01', '14:30:00', 'SALIDA', 13.2, 'Nublado'),
(4, '2025-12-02', '07:42:00', 'ENTRADA', 9.8, 'Lluvioso'),
(4, '2025-12-02', '14:45:00', 'SALIDA', 12.4, 'Parcialmente nublado');s