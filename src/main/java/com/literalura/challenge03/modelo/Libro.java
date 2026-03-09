package com.literalura.challenge03.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibro;

    @Column(nullable = false)
    private String titulo;

    @ManyToMany(mappedBy = "libros", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Autor> autores;

    private String idioma;

    private int numeroDescargas;
}
