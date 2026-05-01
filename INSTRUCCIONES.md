# IroncladBox CrossFit - Aplicación de Escritorio

Aplicación Java MVC para la gestión de un gimnasio de CrossFit con sistema de login y roles (Administrador, Entrenador, Atleta).

## Requisitos Previos

- **Java JDK 17+**
- **PostgreSQL 12+**

## Instalación de la Base de Datos

1. **Crear la base de datos:**
```sql
CREATE DATABASE ironcladbox;
```

2. **Ejecutar el script de esquema:**
```bash
psql -U postgres -d ironcladbox -f database/schema.sql
```

3. **Credenciales por defecto:**
   - Usuario PostgreSQL: `postgres`
   - Contraseña: `postgres`
   - Host: `localhost`
   - Puerto: `5432`

## Compilación

```bash
cd src
javac -encoding UTF-8 -d ../bin com/ironcladbox/**/*.java
cd ..
```

## Ejecución

```bash
java -cp bin com.ironcladbox.view.LoginView
```

## Usuarios de Prueba

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Admin | admin@ironcladbox.com | admin123 |
| Entrenador | jorge@ironcladbox.com | trainer123 |
| Entrenador | maria@ironcladbox.com | trainer123 |
| Atleta | juan@gmail.com | athlete123 |
| Atleta | ana@gmail.com | athlete123 |

## Estructura del Proyecto

```
src/com/ironcladbox/
├── model/          # Entidades (Usuario, Atleta, Entrenador, etc.)
├── view/           # Vistas Swing
├── controller/     # Controladores MVC
├── dao/            # Acceso a datos (DAOs)
└── util/           # Utilidades
```

## Características

- ✓ Sistema de Login con 3 roles
- ✓ Dashboard personalizado por rol
- ✓ Gestión de Atletas, Entrenadores y Clases
- ✓ Membresías y Suscripciones
- ✓ Registros de Asistencia
- ✓ Interfaz Swing moderna (tema oscuro)

## Membresías

- Básica: $29.99/mes
- Estándar: $49.99/mes
- Premium: $99.99/mes
- Anual: $500/año

## Tecnologías

- Java 17+
- Swing (GUI)
- PostgreSQL (BD)
- JDBC (conexión)
- Patrón MVC + POO
