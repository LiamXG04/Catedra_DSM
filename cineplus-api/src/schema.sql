SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  correo VARCHAR(160) NOT NULL UNIQUE,
  contrasena_hash VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS peliculas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(200) NOT NULL,
  url_poster VARCHAR(500),
  sinopsis TEXT NULL,
  generos VARCHAR(200) NULL,
  duracion_min INT,
  clasificacion VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS salas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  capacidad INT NOT NULL
);

CREATE TABLE IF NOT EXISTS funciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_pelicula INT NOT NULL,
  id_sala INT NOT NULL,
  fecha_inicio BIGINT NOT NULL,
  CONSTRAINT fk_funcion_pelicula FOREIGN KEY (id_pelicula) REFERENCES peliculas(id) ON DELETE CASCADE,
  CONSTRAINT fk_funcion_sala FOREIGN KEY (id_sala) REFERENCES salas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS asientos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_sala INT NOT NULL,
  fila INT NOT NULL,
  columna INT NOT NULL,
  UNIQUE KEY unico_sala_fila_columna (id_sala, fila, columna),
  CONSTRAINT fk_asiento_sala FOREIGN KEY (id_sala) REFERENCES salas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bloqueos_asientos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_funcion INT NOT NULL,
  id_asiento INT NOT NULL,
  id_usuario INT NOT NULL,
  bloqueado_en BIGINT NOT NULL,
  expira_en BIGINT NOT NULL,
  UNIQUE KEY unico_funcion_asiento (id_funcion, id_asiento),
  CONSTRAINT fk_bloqueo_funcion FOREIGN KEY (id_funcion) REFERENCES funciones(id) ON DELETE CASCADE,
  CONSTRAINT fk_bloqueo_asiento FOREIGN KEY (id_asiento) REFERENCES asientos(id) ON DELETE CASCADE,
  CONSTRAINT fk_bloqueo_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pedidos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  total DECIMAL(10,2) NOT NULL DEFAULT 0,
  estado VARCHAR(20) NOT NULL DEFAULT 'PAGADO',
  creado_en BIGINT NOT NULL,
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS boletos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido INT NOT NULL,
  id_funcion INT NOT NULL,
  id_asiento INT NOT NULL,
  codigo_qr VARCHAR(64) NOT NULL,
  emitido_en BIGINT NOT NULL,
  UNIQUE KEY unico_boleto_funcion_asiento (id_funcion, id_asiento),
  CONSTRAINT fk_boleto_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE,
  CONSTRAINT fk_boleto_funcion FOREIGN KEY (id_funcion) REFERENCES funciones(id) ON DELETE CASCADE,
  CONSTRAINT fk_boleto_asiento FOREIGN KEY (id_asiento) REFERENCES asientos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS golosinas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  precio DECIMAL(6,2) NOT NULL,
  stock INT DEFAULT 0,
  imagen_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS detalle_pedido (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido INT NOT NULL,
  id_golosina INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(6,2) NOT NULL,
  subtotal DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
  CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_golosina FOREIGN KEY (id_golosina) REFERENCES golosinas(id) ON DELETE CASCADE
);

-- Tabla para el reparto de las películas
CREATE TABLE IF NOT EXISTS reparto (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_pelicula INT NOT NULL,
  nombre_actor VARCHAR(120) NOT NULL,
  personaje VARCHAR(120) NULL,
  CONSTRAINT fk_reparto_pelicula FOREIGN KEY (id_pelicula)
    REFERENCES peliculas(id)
    ON DELETE CASCADE
);

-- NOTA: Los datos de ejemplo (películas y reparto) se movieron a `src/seed.js`
-- para evitar que `schema.sql` inserte filas cada vez que se ejecuta.
