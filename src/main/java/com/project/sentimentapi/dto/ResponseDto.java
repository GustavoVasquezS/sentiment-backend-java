package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ResponseDto {
    private String prevision;
   // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private Integer estrellas;
    private Double probabilidad;
   // private String calificación;
}
