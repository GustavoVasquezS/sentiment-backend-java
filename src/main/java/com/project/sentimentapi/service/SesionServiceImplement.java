package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ComentarioDto;
import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.entity.Comentario;
import com.project.sentimentapi.entity.Sesion;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.SesionRepository;
import com.project.sentimentapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SesionServiceImplement implements SesionService {

    @Autowired
    SesionRepository sesionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SentimentService sentimentService;

    @Override
    public void guardarSesion(SesionDto sesionDto, Integer usuarioId) {
        Optional<User> usuario = userRepository.findById(usuarioId);

        if (usuario.isPresent()) {
            Sesion sesion = new Sesion(
                    LocalDate.parse(sesionDto.getFecha()),
                    sesionDto.getAvgScore(),
                    sesionDto.getTotal(),
                    sesionDto.getPositivos(),
                    sesionDto.getNegativos(),
                    sesionDto.getNeutrales(),
                    usuario.get()
            );

            sesionRepository.save(sesion);
        }
    }

    @Override
    public List<SesionDto> obtenerSesionesPorUsuario(Integer usuarioId) {
        Optional<User> usuario = userRepository.findById(usuarioId);

        if (usuario.isPresent()) {
            List<Sesion> sesiones = sesionRepository.findByUsuarioOrderByFechaDesc(usuario.get());

            return sesiones.stream()
                    .map(sesion -> {
                        // ✅ Mapear comentarios a DTOs
                        List<ComentarioDto> comentariosDto = sesion.getComentarios().stream()
                                .map(c -> new ComentarioDto(c.getTexto(), c.getSentimiento(), c.getProbabilidad()))
                                .collect(Collectors.toList());

                        return new SesionDto(
                                sesion.getSesionId(),
                                sesion.getFecha().toString(),
                                sesion.getAvgScore(),
                                sesion.getTotal(),
                                sesion.getPositivos(),
                                sesion.getNegativos(),
                                sesion.getNeutrales(),
                                comentariosDto // ✅ NUEVO: Incluir comentarios
                        );
                    })
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Override
    public SesionDto analizarYGuardarComentarios(List<String> comentarios, Integer usuarioId) {
        Optional<User> usuario = userRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            return null;
        }

        // Concatenar todos los comentarios con salto de línea
        String textoCompleto = String.join("\n", comentarios);

        // Llamar al servicio de análisis batch
        Optional<com.project.sentimentapi.dto.SentimentsResponseDto> responseOpt =
                sentimentService.consultarSentimientos(textoCompleto);

        if (responseOpt.isEmpty()) {
            return null;
        }

        List<ResponseDto> resultados = responseOpt.get().getResults();

        // Calcular métricas
        int total = resultados.size();
        int positivos = 0;
        int negativos = 0;
        int neutrales = 0;
        double sumaScores = 0.0;

        for (ResponseDto resultado : resultados) {
            String sentimiento = resultado.getPrevision();
            double probabilidad = resultado.getProbabilidad();

            sumaScores += probabilidad;

            if (sentimiento.equalsIgnoreCase("Positivo")) {
                positivos++;
            } else if (sentimiento.equalsIgnoreCase("Negativo")) {
                negativos++;
            } else {
                neutrales++;
            }
        }

        double avgScore = total > 0 ? sumaScores / total : 0.0;

        // ✅ CREAR SESIÓN
        Sesion sesion = new Sesion(
                LocalDate.now(),
                avgScore,
                total,
                positivos,
                negativos,
                neutrales,
                usuario.get()
        );

        // ✅ GUARDAR CADA COMENTARIO ANALIZADO
        List<Comentario> comentariosEntidades = new ArrayList<>();
        List<ComentarioDto> comentariosDto = new ArrayList<>();

        for (int i = 0; i < comentarios.size(); i++) {
            String textoComentario = comentarios.get(i);
            ResponseDto resultado = resultados.get(i);

            Comentario comentarioEntity = new Comentario(
                    textoComentario,
                    resultado.getPrevision(),
                    resultado.getProbabilidad(),
                    sesion
            );

            comentariosEntidades.add(comentarioEntity);

            // ✅ Para el DTO de respuesta
            comentariosDto.add(new ComentarioDto(
                    textoComentario,
                    resultado.getPrevision(),
                    resultado.getProbabilidad()
            ));
        }

        sesion.setComentarios(comentariosEntidades);

        // ✅ GUARDAR SESIÓN CON COMENTARIOS
        Sesion sesionGuardada = sesionRepository.save(sesion);

        // ✅ RETORNAR DTO CON COMENTARIOS INDIVIDUALES
        return new SesionDto(
                sesionGuardada.getSesionId(),
                LocalDate.now().toString(),
                avgScore,
                total,
                positivos,
                negativos,
                neutrales,
                comentariosDto // ✅ AQUÍ ESTÁN LOS COMENTARIOS INDIVIDUALES
        );
    }
}