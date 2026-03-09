package com.literalura.challenge03.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespuestaAutorAPI {

    @JsonProperty("birth_year")
    private Integer fechaNacimiento;

    @JsonProperty("death_year")
    private Integer fechaFallecimiento;

    @JsonProperty("name")
    private String nombre;
}
