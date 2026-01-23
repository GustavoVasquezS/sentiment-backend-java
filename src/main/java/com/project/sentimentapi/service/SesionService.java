package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.SesionDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SesionService {
    void guardarSesion(SesionDto sesionDto, Integer usuarioId);
    List<SesionDto> obtenerSesionesPorUsuario(Integer usuarioId);
    SesionDto analizarYGuardarComentarios(List<String> comentarios, Integer usuarioId);

    @Transactional
    SesionDto analizarYGuardarConProducto(
            List<String> comentarios,
            Integer usuarioId,
            Integer productoId
    );
}