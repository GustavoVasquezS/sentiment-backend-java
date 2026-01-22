package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDto {
    private String texto;
    private String sentimiento;
    private Double probabilidad;
}