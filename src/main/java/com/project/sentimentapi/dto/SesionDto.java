package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
// ✅ AGREGAR ESTOS CAMPOS A SesionDto EXISTENTE

    private Integer productoId;
    private String nombreProducto;

    // ✅ Estadísticas del producto en ESTA sesión
    private ProductoMencionesDto productoMenciones;
    // ✅ NUEVO: Lista de comentarios analizados
    private List<ComentarioDto> comentarios;

    public SesionDto(Integer sesionId, String string, Double avgScore, Integer total, Integer positivos, Integer negativos, Integer neutrales, List<ComentarioDto> comentariosDto) {
    }

    public void setProductosDetectados(List<ProductoMencionesDto> productosDetectados) {
    }
}