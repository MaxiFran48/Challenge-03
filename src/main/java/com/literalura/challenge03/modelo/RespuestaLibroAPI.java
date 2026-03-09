package com.literalura.challenge03.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespuestaLibroAPI {
    @JsonProperty("title")
    private String titulo;

    @JsonProperty("authors")
    private List<RespuestaAutorAPI> autores;

    @JsonProperty("languages")
    private List<String> idiomas;

    @JsonProperty("download_count")
    private int cantidadDescargas;


}
