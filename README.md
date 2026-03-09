# 📚 LiterAlura

Aplicación de consola desarrollada en Java con Spring Boot que permite buscar libros utilizando la API de [Gutendex](https://gutendex.com/) y almacenarlos en una base de datos PostgreSQL.

## Funcionalidades

| Opción | Descripción |
|--------|-------------|
| 1 | **Buscar libros por título** — Consulta la API de Gutendex y registra el libro con sus autores en la base de datos |
| 2 | **Listar libros registrados** — Muestra todos los libros almacenados en la base de datos |
| 3 | **Listar autores registrados** — Muestra todos los autores almacenados en la base de datos |
| 4 | **Listar autores vivos en un año dado** — Filtra autores cuyo período de vida incluye el año ingresado |
| 5 | **Listar libros por idioma** — Filtra libros registrados por código de idioma (es, en, fr, pt) |

## Tecnologías

- Java 21
- Spring Boot 4.0.3
- Spring Data JPA / Hibernate
- PostgreSQL
- Jackson (JSON)
- Lombok
- Gradle

## Requisitos previos

- JDK 21+
- PostgreSQL instalado y en ejecución
- Gradle (o usar el wrapper incluido `./gradlew`)

## Configuración

1. **Iniciar el servicio de PostgreSQL:**

   ```bash
   sudo systemctl start postgresql
   ```

2. **Crear la base de datos:**

   ```bash
   sudo -u postgres psql -c "CREATE DATABASE literalura;"
   ```

3. **Configurar las credenciales** en `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
   ```

## Ejecución

```bash
./gradlew bootRun
```

## Estructura del proyecto

```
src/main/java/com/literalura/challeng03/
├── Challeng03Application.java          # Punto de entrada y menú principal
├── controlador/
│   └── LiteraluraControlador.java      # Lógica de cada opción del menú
├── modelo/
│   ├── Libro.java                      # Entidad JPA - Libros
│   ├── Autor.java                      # Entidad JPA - Autores
│   ├── RespuestaGutendexAPI.java       # DTO - Respuesta raíz de la API
│   ├── RespuestaLibroAPI.java          # DTO - Datos del libro desde la API
│   └── RespuestaAutorAPI.java          # DTO - Datos del autor desde la API
└── repositorio/
    ├── LibroRepositorio.java           # Spring Data JPA - Consultas de libros
    └── AutorRepositorio.java           # Spring Data JPA - Consultas de autores
```

## Modelo de datos

La relación entre libros y autores es **muchos a muchos**, gestionada mediante una tabla intermedia `autor_libro`. Hibernate crea las tablas automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`.
