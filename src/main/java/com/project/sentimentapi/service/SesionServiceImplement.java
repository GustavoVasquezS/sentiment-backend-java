package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.entity.Sesion;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.SesionRepository;
import com.project.sentimentapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                    .map(sesion -> new SesionDto(
                            sesion.getSesionId(),
                            sesion.getFecha().toString(),
                            sesion.getAvgScore(),
                            sesion.getTotal(),
                            sesion.getPositivos(),
                            sesion.getNegativos(),
                            sesion.getNeutrales()
                    ))
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
        String fechaActual = LocalDate.now().toString();

        // Crear y guardar la sesión
        Sesion sesion = new Sesion(
                LocalDate.now(),
                avgScore,
                total,
                positivos,
                negativos,
                neutrales,
                usuario.get()
        );

        Sesion sesionGuardada = sesionRepository.save(sesion);

        // Retornar DTO con el ID generado
        return new SesionDto(
                sesionGuardada.getSesionId(),
                fechaActual,
                avgScore,
                total,
                positivos,
                negativos,
                neutrales
        );
    }
}