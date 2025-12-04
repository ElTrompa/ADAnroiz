CREATE TABLE IF NOT EXISTS trabajadores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  dni VARCHAR(20) UNIQUE NOT NULL,
  email VARCHAR(100),
  password VARCHAR(255) NOT NULL DEFAULT '1234',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--------------------------------------------------------

CREATE TABLE IF NOT EXISTS clima_dia (
  fecha DATE PRIMARY KEY,
  descripcion VARCHAR(100),
  temp_min DECIMAL(5,2),
  temp_max DECIMAL(5,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--------------------------------------------------------

CREATE TABLE IF NOT EXISTS fichajes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  trabajador_id INT NOT NULL,
  hora DATETIME NOT NULL,
  tipo ENUM('ENTRADA','SALIDA') NOT NULL,
  clima_fecha DATE,

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (trabajador_id) REFERENCES trabajadores(id) ON DELETE CASCADE,
  FOREIGN KEY (clima_fecha) REFERENCES clima_dia(fecha) ON DELETE SET NULL,

  INDEX idx_trabajador_hora (trabajador_id, hora)
);

---------------------------------------------------------------------------------

-- Trabajadores
INSERT INTO trabajadores (nombre, apellidos, dni, email, password) VALUES
('Juan', 'Garcia Lopez', '12345678A', 'juan.garcia@empresa.com', '1234'),
('María', 'Rodríguez Pérez', '87654321B', 'maria.rodriguez@empresa.com', '1234'),
('Carlos', 'Martínez Sánchez', '11223344C', 'carlos.martinez@empresa.com', '1234');

-- Clima de los días
INSERT INTO clima_dia (fecha, descripcion, temp_min, temp_max) VALUES
('2025-12-01', 'Nublado con intervalos de sol', 14.5, 19.0),
('2025-12-02', 'Lluvioso', 12.0, 15.5);

-- Fichajes
INSERT INTO fichajes (trabajador_id, hora, tipo, clima_fecha) VALUES
(1, '2025-12-01 08:00:00', 'ENTRADA', '2025-12-01'),
(1, '2025-12-01 14:00:00', 'SALIDA',  '2025-12-01'),
(1, '2025-12-01 15:00:00', 'ENTRADA', '2025-12-01'),
(1, '2025-12-01 18:30:00', 'SALIDA',  '2025-12-01'),
(2, '2025-12-01 09:00:00', 'ENTRADA', '2025-12-01'),
(2, '2025-12-01 17:00:00', 'SALIDA',  '2025-12-01'),
(3, '2025-12-02 08:30:00', 'ENTRADA', '2025-12-02'),
(3, '2025-12-02 16:30:00', 'SALIDA',  '2025-12-02');