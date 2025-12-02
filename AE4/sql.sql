CREATE TABLE trabajador (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(150) NOT NULL,
  puesto VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fichaje (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_trabajador BIGINT NOT NULL,
  hora DATETIME NOT NULL,
  tipo ENUM('E','S') NOT NULL,     -- Entrada / Salida
  clima VARCHAR(50),               -- Ej: despejado, nublado, soleado...
  temperatura DECIMAL(5,2),        -- Ej: 22.50 ºC
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_trabajador) REFERENCES trabajador(id) ON DELETE CASCADE
);

CREATE INDEX idx_fichaje_trabajador_hora 
ON fichaje(id_trabajador, hora);

INSERT INTO trabajador (nombre, apellidos, puesto)
VALUES 
('Maria', 'Lopez Garcia', 'Administración'),
('Juan', 'Perez Soto', 'Operario');

INSERT INTO fichaje (id_trabajador, hora, tipo, clima, temperatura)
VALUES
(1, NOW(), 'E', 'nublado', 18.4),
(2, NOW(), 'E', 'soleado', 22.8);
