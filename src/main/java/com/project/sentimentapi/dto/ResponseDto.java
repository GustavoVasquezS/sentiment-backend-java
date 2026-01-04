package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto {
    // private String texto;
    private String prevision;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer estrellas;
    private Double probabilidad;
    private String calificación;

}
