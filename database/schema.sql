-- IroncladBox CrossFit Database Schema
-- PostgreSQL

-- Crear tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    id_rol INTEGER NOT NULL REFERENCES roles(id_rol),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear tabla de membresías
CREATE TABLE IF NOT EXISTS membresias (
    id_membresia SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10, 2) NOT NULL,
    duracion_dias INTEGER NOT NULL,
    beneficios TEXT,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de atletas
CREATE TABLE IF NOT EXISTS atletas (
    id_atleta SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL UNIQUE REFERENCES usuarios(id_usuario),
    peso DECIMAL(5, 2),
    altura DECIMAL(4, 2),
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de suscripciones de atletas
CREATE TABLE IF NOT EXISTS suscripciones (
    id_suscripcion SERIAL PRIMARY KEY,
    id_atleta INTEGER NOT NULL REFERENCES atletas(id_atleta),
    id_membresia INTEGER NOT NULL REFERENCES membresias(id_membresia),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de entrenadores
CREATE TABLE IF NOT EXISTS entrenadores (
    id_entrenador SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL UNIQUE REFERENCES usuarios(id_usuario),
    certificacion VARCHAR(200),
    especialidad VARCHAR(100),
    experiencia_anios INTEGER,
    fecha_contratacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de clases
CREATE TABLE IF NOT EXISTS clases (
    id_clase SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    id_entrenador INTEGER NOT NULL REFERENCES entrenadores(id_entrenador),
    horario_inicio TIME NOT NULL,
    horario_fin TIME NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    capacidad_maxima INTEGER NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de asistencias
CREATE TABLE IF NOT EXISTS asistencias (
    id_asistencia SERIAL PRIMARY KEY,
    id_atleta INTEGER NOT NULL REFERENCES atletas(id_atleta),
    id_clase INTEGER NOT NULL REFERENCES clases(id_clase),
    fecha DATE NOT NULL,
    presente BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de contactos
CREATE TABLE IF NOT EXISTS contactos (
    id_contacto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    mensaje TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar roles
INSERT INTO roles (nombre, descripcion) VALUES
('ADMINISTRADOR', 'Administrador del sistema'),
('ENTRENADOR', 'Entrenador de clases'),
('ATLETA', 'Atleta o miembro');

-- Insertar membresías
INSERT INTO membresias (nombre, descripcion, precio, duracion_dias, beneficios) VALUES
('Básica', 'Acceso a clases grupales', 29.99, 30, 'Clases grupales ilimitadas'),
('Estándar', 'Acceso completo al gimnasio', 49.99, 30, 'Clases grupales + equipo libre'),
('Premium', 'Acceso VIP con entrenador personal', 99.99, 30, 'Todo incluido + 2 sesiones personales'),
('Anual', 'Suscripción anual con descuento', 500.00, 365, 'Acceso completo + beneficios extras');

-- Insertar usuario administrador de prueba
INSERT INTO usuarios (email, contrasena, nombre, apellido, telefono, id_rol, activo) VALUES
('admin@ironcladbox.com', 'admin123', 'Admin', 'IroncladBox', '+593999999999', 1, TRUE);

-- Insertar entrenadores de prueba
INSERT INTO usuarios (email, contrasena, nombre, apellido, telefono, id_rol, activo) VALUES
('jorge@ironcladbox.com', 'trainer123', 'Jorge', 'Pérez', '+593998888888', 2, TRUE),
('maria@ironcladbox.com', 'trainer123', 'María', 'López', '+593997777777', 2, TRUE);

-- Insertar entrenadores (tabla entrenadores)
INSERT INTO entrenadores (id_usuario, certificacion, especialidad, experiencia_anios) VALUES
(2, 'CrossFit L2', 'Fuerza y Potencia', 5),
(3, 'CrossFit L1', 'Flexibilidad', 3);

-- Insertar atletas de prueba
INSERT INTO usuarios (email, contrasena, nombre, apellido, telefono, id_rol, activo) VALUES
('juan@gmail.com', 'athlete123', 'Juan', 'Martínez', '+593999111111', 3, TRUE),
('ana@gmail.com', 'athlete123', 'Ana', 'García', '+593999222222', 3, TRUE);

-- Insertar atletas (tabla atletas)
INSERT INTO atletas (id_usuario, peso, altura) VALUES
(4, 80.5, 1.80),
(5, 65.0, 1.68);

-- Insertar suscripciones
INSERT INTO suscripciones (id_atleta, id_membresia, fecha_inicio, fecha_fin, activa) VALUES
(1, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE),
(2, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE);

-- Insertar clases
INSERT INTO clases (nombre, descripcion, id_entrenador, horario_inicio, horario_fin, dia_semana, capacidad_maxima, activa) VALUES
('WOD - Workout of the Day', 'Entrenamiento funcional completo', 1, '06:00', '07:00', 'Lunes', 20, TRUE),
('Fuerza y Potencia', 'Desarrollo de fuerza con pesas', 1, '18:00', '19:00', 'Martes', 15, TRUE),
('Yoga y Flexibilidad', 'Mejora flexibilidad y recuperación', 2, '19:00', '20:00', 'Miércoles', 12, TRUE),
('HIIT', 'Entrenamiento de alta intensidad', 2, '07:00', '08:00', 'Viernes', 20, TRUE);
