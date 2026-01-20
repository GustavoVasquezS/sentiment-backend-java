package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.ComentariosRequestDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.service.SesionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sesion")
public class SesionController {

    @Autowired
    SesionService sesionService;

    @PostMapping
    public ResponseEntity<?> guardarSesion(
            HttpServletRequest request,
            @RequestBody SesionDto sesionDto) {

        // Extraer usuarioId del request (inyectado por JwtAuthenticationFilter)
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        sesionService.guardarSesion(sesionDto, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SesionDto>> obtenerSesiones(HttpServletRequest request) {
        // Extraer usuarioId del request (inyectado por JwtAuthenticationFilter)
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        List<SesionDto> sesiones = sesionService.obtenerSesionesPorUsuario(usuarioId);
        return ResponseEntity.ok(sesiones);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<SesionDto>> obtenerHistorialSesiones(HttpServletRequest request) {
        // Extraer usuarioId del request (inyectado por JwtAuthenticationFilter)
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        List<SesionDto> sesiones = sesionService.obtenerSesionesPorUsuario(usuarioId);
        return ResponseEntity.ok(sesiones);
    }

    @PostMapping("/analizar")
    public ResponseEntity<?> analizarComentarios(
            HttpServletRequest request,
            @RequestBody ComentariosRequestDto comentariosRequest) {

        // Extraer usuarioId del request (inyectado por JwtAuthenticationFilter)
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        SesionDto sesion = sesionService.analizarYGuardarComentarios(
                comentariosRequest.getComentarios(),
                usuarioId
        );

        if (sesion != null) {
            return ResponseEntity.ok(sesion);
        } else {
            return ResponseEntity.status(502).body("Error al analizar los comentarios");
        }
    }
}