package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.SesionDto;

import java.util.List;

public interface SesionService {
    void guardarSesion(SesionDto sesionDto, Integer usuarioId);
    List<SesionDto> obtenerSesionesPorUsuario(Integer usuarioId);
    SesionDto analizarYGuardarComentarios(List<String> comentarios, Integer usuarioId);
}