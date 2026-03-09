# Conversación - Configuración de LiterAlura con PostgreSQL y JPA
**Fecha:** 2026-03-09

---

## 1. Configurar PostgreSQL con JPA

El proyecto ya tenía las dependencias necesarias en `build.gradle` (`spring-boot-starter-data-jpa` y `org.postgresql:postgresql`). Se agregó la configuración de conexión en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## 2. Crear la base de datos en PostgreSQL

Opciones para crear la DB:
- Terminal: `psql -U tu_usuario -c "CREATE DATABASE literalura;"`
- Dentro de psql: `CREATE DATABASE literalura;`
- pgAdmin: click derecho en Databases → Create → Database

---

## 3. Problema: el servicio de PostgreSQL no arrancaba

**Error:** `Job for postgresql.service failed because the control process exited with error code.`

**Causa:** El directorio de datos `/var/lib/postgres/data` no estaba inicializado.

**Solución:**
```bash
sudo su -l postgres -c "initdb --locale=C.UTF-8 --encoding=UTF8 -D '/var/lib/postgres/data'"
sudo systemctl start postgresql
```

Se recomendó usar `systemctl` en lugar de `pg_ctl` por ser la forma estándar en sistemas con systemd.

---

## 4. Configuración de entidades JPA y repositorios

Se configuraron las entidades `Libro` y `Autor` con anotaciones JPA:
- Relación `@ManyToMany` entre ambas entidades
- Tabla intermedia `autor_libro`
- `fechaNacimiento`/`fechaFallecimiento` cambiados de `Date` a `Integer` (la API devuelve solo el año)

Repositorios creados con Spring Data JPA:
- `LibroRepositorio` — métodos: `findByTituloIgnoreCase()`, `findByIdiomaIgnoreCase()`
- `AutorRepositorio` — métodos: `findByNombreIgnoreCase()`, buscar autores vivos por año

Se reemplazaron las clases `RepositorioSQL` y `RepositorioPostgreSQL` por interfaces JPA.

---

## 5. Completar métodos del controlador

Se implementaron los 5 métodos del `LiteraluraControlador`:
- `buscarLibroPorTitulo()` → consulta la API de Gutendex, guarda en DB
- `listarLibrosRegistrados()` → consulta la DB
- `listarAutoresRegistrados()` → consulta la DB
- `listarAutoresVivosPorAño()` → filtra autores por año en la DB
- `listarLibrosPorIdioma()` → filtra libros por idioma en la DB

Se creó `RespuestaGutendexAPI` como wrapper porque la API devuelve `{ "results": [...] }`.

---

## 6. Errores resueltos durante la implementación

### Error: `NoClassDefFoundError: com/fasterxml/jackson/annotation/JsonSerializeAs`
**Causa:** Conflicto de versiones entre `jackson-databind:3.1.0` (agregado manualmente) y las annotations.
**Solución:** Se eliminó la dependencia manual y se agregó `spring-boot-starter-json` para que Spring Boot maneje las versiones de Jackson.

### Error: `No content to map due to end-of-input`
**Causa:** La URL `gutendex.com/books` redirige a `gutendex.com/books/` y el HttpClient no seguía redirects.
**Solución:** Se agregó `/` al final de la URL y se configuró `HttpClient.Redirect.NORMAL`.

### Error: `TransientPropertyValueException: references an unsaved transient instance`
**Causa:** Se intentaba guardar el Autor con referencia a un Libro no persistido.
**Solución:** Se agregó `libroRepositorio.save(libro)` antes de guardar los autores.

---

## 7. Opinión sobre arquitectura y buenas prácticas

Mejoras sugeridas:
1. **Capa de servicio** — Separar lógica de negocio del controlador en `LibroServicio`
2. **Cliente API separado** — Extraer consumo de Gutendex a `GutendexCliente`
3. **No usar Scanner en el controlador** — Recibir parámetros ya parseados
4. **Manejo de errores específico** — Distinguir errores de red, parseo y DB
5. **Variables de entorno** para credenciales — `${DB_USER}`, `${DB_PASSWORD}`
6. **`@Transactional`** en operaciones de escritura

Arquitectura ideal: `Controlador → Servicio → Repositorio` + `Cliente API`

---

## 8. Seguridad al subir a GitHub

Se actualizó `.gitignore` para excluir:
- `application.properties` (contiene credenciales)
- `node_modules/`, `*.log`, `logfile`

Se creó `application.properties.example` como plantilla con variables de entorno.
