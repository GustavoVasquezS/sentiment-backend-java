package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionDto {
    @JsonProperty("sessionId")
    private Integer sesionId;

    @JsonProperty("date")
    private String fecha;

    private Double avgScore;
    private Integer total;
    private Integer positivos;
    private Integer negativos;
    private Integer neutrales;
}