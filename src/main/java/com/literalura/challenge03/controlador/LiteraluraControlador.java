package com.literalura.challenge03.controlador;

import com.literalura.challenge03.modelo.*;
import com.literalura.challenge03.repositorio.AutorRepositorio;
import com.literalura.challenge03.repositorio.LibroRepositorio;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Controller
public class LiteraluraControlador {

    private final LibroRepositorio libroRepositorio;
    private final AutorRepositorio autorRepositorio;
    private final Scanner input = new Scanner(System.in);

    @Autowired
    public LiteraluraControlador(LibroRepositorio libroRepositorio, AutorRepositorio autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void buscarLibroPorTitulo() {
        System.out.print("Ingrese el titulo del libro deseado: ");
        String titulo = input.nextLine();

        String url = "https://gutendex.com/books/?search="
                + URLEncoder.encode(titulo, StandardCharsets.UTF_8);

        HttpClient cliente = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            RespuestaGutendexAPI gutendex = mapper.readValue(respuesta.body(), RespuestaGutendexAPI.class);

            if (gutendex.getResultados() == null || gutendex.getResultados().isEmpty()) {
                System.out.println("No se encontraron libros con ese titulo.");
                return;
            }

            RespuestaLibroAPI primerResultado = gutendex.getResultados().get(0);

            Optional<Libro> libroExistente = libroRepositorio.findByTituloIgnoreCase(primerResultado.getTitulo());
            if (libroExistente.isPresent()) {
                System.out.println("El libro ya esta registrado en la base de datos.");
                imprimirLibro(libroExistente.get());
                return;
            }

            Libro libro = new Libro();
            libro.setTitulo(primerResultado.getTitulo());
            libro.setIdioma(primerResultado.getIdiomas() != null && !primerResultado.getIdiomas().isEmpty()
                    ? primerResultado.getIdiomas().get(0) : "desconocido");
            libro.setNumeroDescargas(primerResultado.getCantidadDescargas());

            List<Autor> autores = new ArrayList<>();
            if (primerResultado.getAutores() != null) {
                for (RespuestaAutorAPI autorAPI : primerResultado.getAutores()) {
                    Optional<Autor> autorExistente = autorRepositorio.findByNombreIgnoreCase(autorAPI.getNombre());
                    Autor autor;
                    if (autorExistente.isPresent()) {
                        autor = autorExistente.get();
                    } else {
                        autor = new Autor();
                        autor.setNombre(autorAPI.getNombre());
                        autor.setFechaNacimiento(autorAPI.getFechaNacimiento());
                        autor.setFechaFallecimiento(autorAPI.getFechaFallecimiento());
                        autor.setLibros(new ArrayList<>());
                    }
                    autor.getLibros().add(libro);
                    autores.add(autor);
                }
            }

            libro.setAutores(autores);

            libroRepositorio.save(libro);
            for (Autor autor : autores) {
                autorRepositorio.save(autor);
            }

            System.out.println("\nLibro registrado exitosamente!");
            imprimirLibro(libro);

        } catch (Exception e) {
            System.out.println("Error durante la obtencion de los libros: " + e.getMessage());
        }
    }

    public void listarLibrosRegistrados() {
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("\n--- Libros registrados ---");
        libros.forEach(this::imprimirLibro);
    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = autorRepositorio.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        System.out.println("\n--- Autores registrados ---");
        autores.forEach(this::imprimirAutor);
    }

    public void listarAutoresVivosPorAño() {
        System.out.print("Ingrese el año para buscar autores vivos: ");
        try {
            int anio = Integer.parseInt(input.nextLine().trim());
            List<Autor> autores = autorRepositorio
                    .findByFechaNacimientoLessThanEqualAndFechaFallecimientoGreaterThanEqual(anio, anio);
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio + ".");
                return;
            }
            System.out.println("\n--- Autores vivos en " + anio + " ---");
            autores.forEach(this::imprimirAutor);
        } catch (NumberFormatException e) {
            System.out.println("Año invalido.");
        }
    }

    public void listarLibrosPorIdioma() {
        System.out.print("""
                Ingrese el idioma para buscar libros:
                  es - Español
                  en - Inglés
                  fr - Francés
                  pt - Portugués
                
                Idioma:\s""");
        String idioma = input.nextLine().trim();
        List<Libro> libros = libroRepositorio.findByIdiomaIgnoreCase(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
            return;
        }
        System.out.println("\n--- Libros en idioma '" + idioma + "' ---");
        libros.forEach(this::imprimirLibro);
    }

    private void imprimirLibro(Libro libro) {
        System.out.println("─────────────────────────────");
        System.out.println("Titulo: " + libro.getTitulo());
        System.out.println("Autor(es): " + (libro.getAutores() != null
                ? String.join(", ", libro.getAutores().stream().map(Autor::getNombre).toList())
                : "Desconocido"));
        System.out.println("Idioma: " + libro.getIdioma());
        System.out.println("Descargas: " + libro.getNumeroDescargas());
        System.out.println("─────────────────────────────\n");
    }

    private void imprimirAutor(Autor autor) {
        System.out.println("─────────────────────────────");
        System.out.println("Nombre: " + autor.getNombre());
        System.out.println("Nacimiento: " + (autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "Desconocido"));
        System.out.println("Fallecimiento: " + (autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "Vivo / Desconocido"));
        System.out.println("Libros: " + (autor.getLibros() != null
                ? String.join(", ", autor.getLibros().stream().map(Libro::getTitulo).toList())
                : "Ninguno"));
        System.out.println("─────────────────────────────\n");
    }
}
