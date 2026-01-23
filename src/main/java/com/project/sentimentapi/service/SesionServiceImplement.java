package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.*;
import com.project.sentimentapi.entity.*;
import com.project.sentimentapi.repository.ProductoRepository;
import com.project.sentimentapi.repository.SesionProductoRepository;
import com.project.sentimentapi.repository.SesionRepository;
import com.project.sentimentapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

import static tools.jackson.databind.type.LogicalType.Map;

@Service
    public class SesionServiceImplement implements SesionService {

        @Autowired
        SesionRepository sesionRepository;

    @Autowired
    SesionProductoRepository sesionProductoRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        SentimentService sentimentService;

        @Autowired
        ProductoRepository productoRepository;

        @Autowired
        ProductoService productoService;


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
        // ✅ AGREGAR ESTE NUEVO MÉTODO A SesionServiceImplement

        @Transactional
        @Override
        public SesionDto analizarYGuardarConProducto(
                List<String> comentarios,
                Integer usuarioId,
                Integer productoId
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // ✅ Validar permisos
            if (!producto.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para usar este producto");
            }

            // 1️⃣ ANÁLISIS NORMAL (reutilizamos tu código)
            String textoCompleto = String.join("\n", comentarios);
            Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

            if (responseOpt.isEmpty()) {
                throw new RuntimeException("Error al analizar comentarios");
            }

            List<ResponseDto> resultados = responseOpt.get().getResults();

            // 2️⃣ CALCULAR ESTADÍSTICAS GENERALES
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

            // 3️⃣ ⚡ ANÁLISIS ESPECÍFICO DEL PRODUCTO (CONTEO CORRECTO)
            String nombreProductoLower = producto.getNombreProducto().toLowerCase();

            int productoPosi = 0;
            int productoNega = 0;
            int productoNeutr = 0;
            int totalMencionesProducto = 0;

            List<Comentario> comentariosEntidades = new ArrayList<>();
            List<ComentarioDto> comentariosDto = new ArrayList<>();

            for (int i = 0; i < comentarios.size(); i++) {
                String textoComentario = comentarios.get(i);
                ResponseDto resultado = resultados.get(i);

                // ✅ VERIFICAR SI EL COMENTARIO MENCIONA EL PRODUCTO
                boolean mencionaProducto = textoComentario.toLowerCase().contains(nombreProductoLower);

                if (mencionaProducto) {
                    totalMencionesProducto++;

                    // ✅ CONTAR POR SENTIMIENTO
                    String sentimiento = resultado.getPrevision();
                    if (sentimiento.equalsIgnoreCase("Positivo")) {
                        productoPosi++;
                    } else if (sentimiento.equalsIgnoreCase("Negativo")) {
                        productoNega++;
                    } else {
                        productoNeutr++;
                    }
                }

                // Guardar comentario en BD
                Sesion sesionTemp = new Sesion(); // Se asignará después
                Comentario comentarioEntity = new Comentario(
                        textoComentario,
                        resultado.getPrevision(),
                        resultado.getProbabilidad(),
                        sesionTemp
                );
                comentariosEntidades.add(comentarioEntity);

                comentariosDto.add(new ComentarioDto(
                        textoComentario,
                        resultado.getPrevision().toLowerCase(),
                        resultado.getProbabilidad()
                ));
            }

            // 4️⃣ CREAR SESIÓN
            Sesion sesion = new Sesion(
                    LocalDate.now(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    usuario
            );

            // ✅ ASOCIAR PRODUCTO A LA SESIÓN
            sesion.setProducto(producto);

            // Asignar sesión a los comentarios
            for (Comentario com : comentariosEntidades) {
                com.setSesion(sesion);
            }
            sesion.setComentarios(comentariosEntidades);

            // 5️⃣ GUARDAR SESIÓN
            Sesion sesionGuardada = sesionRepository.save(sesion);

            // 6️⃣ ⚡ ACTUALIZAR CONTADORES DEL PRODUCTO (TRANSACCIONAL)
            productoService.actualizarContadoresProducto(
                    productoId,
                    productoPosi,
                    productoNega,
                    productoNeutr
            );

            // 7️⃣ PREPARAR RESPUESTA CON INFO DEL PRODUCTO
            ProductoMencionesDto productoMenciones = new ProductoMencionesDto(
                    producto.getNombreProducto(),
                    totalMencionesProducto,
                    productoPosi,
                    productoNega,
                    productoNeutr,
                    total > 0 ? (totalMencionesProducto * 100.0) / total : 0.0
            );

            SesionDto sesionDto = new SesionDto(
                    sesionGuardada.getSesionId(),
                    LocalDate.now().toString(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    comentariosDto
            );

            // ✅ AGREGAR INFO DEL PRODUCTO
            sesionDto.setProductoId(producto.getProductoId());
            sesionDto.setNombreProducto(producto.getNombreProducto());
            sesionDto.setProductoMenciones(productoMenciones);

            return sesionDto;
        }

        @Override
        public SesionPreviaInfoDto obtenerProductosUltimaSesion(Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Sesion> sesiones = sesionRepository.findByUsuarioOrderByFechaDesc(usuario);

            if (sesiones.isEmpty()) {
                return null; // No hay sesiones previas
            }

            Sesion ultimaSesion = sesiones.get(0);
            List<SesionProducto> productosUsados = sesionProductoRepository.findBySesion(ultimaSesion);

            if (productosUsados.isEmpty()) {
                return null; // La última sesión no tenía productos
            }

            List<ProductoPrevioDto> productosDto = productosUsados.stream()
                    .map(sp -> new ProductoPrevioDto(
                            sp.getProducto().getProductoId(),
                            sp.getProducto().getNombreProducto(),
                            sp.getProducto().getCategoria().getNombreCategoria(),
                            sp.getMencionesSesion(),
                            sp.getPositivosSesion(),
                            sp.getNegativosSesion()
                    ))
                    .collect(Collectors.toList());

            return new SesionPreviaInfoDto(
                    ultimaSesion.getSesionId(),
                    ultimaSesion.getFecha().toString(),
                    productosDto.size(),
                    productosDto
            );
        }

        /**
         * ✨ NUEVO: Analizar con los mismos productos de una sesión previa
         */
        @Override
        @Transactional
        public SesionDto analizarConMismosProductos(
                List<String> comentarios,
                Integer usuarioId,
                Integer sesionPreviaId
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Sesion sesionPrevia = sesionRepository.findById(sesionPreviaId)
                    .orElseThrow(() -> new RuntimeException("Sesión previa no encontrada"));

            // Verificar que la sesión pertenezca al usuario
            if (!sesionPrevia.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para acceder a esta sesión");
            }

            // Obtener productos usados en esa sesión
            List<SesionProducto> productosUsados = sesionProductoRepository.findBySesion(sesionPrevia);

            if (productosUsados.isEmpty()) {
                throw new RuntimeException("La sesión no tiene productos asociados");
            }

            List<Integer> productosIds = productosUsados.stream()
                    .map(sp -> sp.getProducto().getProductoId())
                    .collect(Collectors.toList());

            // Reutilizar el método de análisis con múltiples productos
            return analizarConMultiplesProductos(comentarios, usuarioId, productosIds);
        }

        /**
         * ✨ NUEVO: Analizar con múltiples productos seleccionados
         */
        @Override
        @Transactional
        public SesionDto analizarConMultiplesProductos(
                List<String> comentarios,
                Integer usuarioId,
                List<Integer> productosIds
        ) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener productos
            List<Producto> productos = new ArrayList<>();
            for (Integer productoId : productosIds) {
                Producto producto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

                if (!producto.getUsuario().getUsuarioID().equals(usuarioId)) {
                    throw new RuntimeException("No tienes permiso para usar el producto: " + producto.getNombreProducto());
                }

                productos.add(producto);
            }

            System.out.println("📊 Analizando con " + productos.size() + " productos:");
            productos.forEach(p -> System.out.println("   - " + p.getNombreProducto()));

            // Análisis de sentimientos
            String textoCompleto = String.join("\n", comentarios);
            Optional<SentimentsResponseDto> responseOpt = sentimentService.consultarSentimientos(textoCompleto);

            if (responseOpt.isEmpty()) {
                throw new RuntimeException("Error al analizar comentarios");
            }

            List<ResponseDto> resultados = responseOpt.get().getResults();

            // Calcular estadísticas generales
            int total = resultados.size();
            int positivos = 0, negativos = 0, neutrales = 0;
            double sumaScores = 0.0;

            for (ResponseDto resultado : resultados) {
                sumaScores += resultado.getProbabilidad();
                String sentimiento = resultado.getPrevision();

                if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
                else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
                else neutrales++;
            }

            double avgScore = total > 0 ? sumaScores / total : 0.0;

            // Detectar productos en comentarios
            HashMap<Integer, ContadorProducto> contadores = new HashMap<>();
            for (Producto p : productos) {
                contadores.put(p.getProductoId(), new ContadorProducto(p.getNombreProducto()));
            }

            List<Comentario> comentariosEntidades = new ArrayList<>();
            List<ComentarioDto> comentariosDto = new ArrayList<>();

            for (int i = 0; i < comentarios.size(); i++) {
                String textoComentario = comentarios.get(i);
                ResponseDto resultado = resultados.get(i);
                String textoLower = textoComentario.toLowerCase();

                // Buscar productos mencionados
                for (Producto producto : productos) {
                    if (textoLower.contains(producto.getNombreProducto().toLowerCase())) {
                        ContadorProducto contador = contadores.get(producto.getProductoId());
                        contador.incrementar(resultado.getPrevision());
                    }
                }

                Sesion sesionTemp = new Sesion();
                comentariosEntidades.add(new Comentario(
                        textoComentario,
                        resultado.getPrevision(),
                        resultado.getProbabilidad(),
                        sesionTemp
                ));

                comentariosDto.add(new ComentarioDto(
                        textoComentario,
                        resultado.getPrevision().toLowerCase(),
                        resultado.getProbabilidad()
                ));
            }

            // Crear sesión
            Sesion sesion = new Sesion(LocalDate.now(), avgScore, total, positivos, negativos, neutrales, usuario);
            for (Comentario com : comentariosEntidades) {
                com.setSesion(sesion);
            }
            sesion.setComentarios(comentariosEntidades);
            Sesion sesionGuardada = sesionRepository.save(sesion);

            // Guardar relación sesión-productos y actualizar contadores
            List<ProductoMencionesDto> productosDetectados = new ArrayList<>();

            for (Map.Entry<Integer, ContadorProducto> entry : contadores.entrySet()) {
                Integer productoId = entry.getKey();
                ContadorProducto contador = entry.getValue();

                if (contador.getTotal() > 0) {
                    // Guardar relación
                    SesionProducto sp = new SesionProducto(
                            sesionGuardada,
                            productoRepository.findById(productoId).get(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );
                    sesionProductoRepository.save(sp);

                    // Actualizar producto
                    productoService.actualizarContadoresProducto(
                            productoId,
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales()
                    );

                    // Agregar a respuesta
                    productosDetectados.add(new ProductoMencionesDto(
                            contador.getNombreProducto(),
                            contador.getTotal(),
                            contador.getPositivos(),
                            contador.getNegativos(),
                            contador.getNeutrales(),
                            total > 0 ? (contador.getTotal() * 100.0) / total : 0.0
                    ));
                }
            }

            SesionDto sesionDto = new SesionDto(
                    sesionGuardada.getSesionId(),
                    LocalDate.now().toString(),
                    avgScore,
                    total,
                    positivos,
                    negativos,
                    neutrales,
                    comentariosDto
            );

            sesionDto.setProductosDetectados(productosDetectados);

            return sesionDto;
        }

        // Clase auxiliar para conteo
        private static class ContadorProducto {
            private final String nombreProducto;
            private int total = 0, positivos = 0, negativos = 0, neutrales = 0;

            public ContadorProducto(String nombreProducto) {
                this.nombreProducto = nombreProducto;
            }

            public void incrementar(String sentimiento) {
                total++;
                if (sentimiento.equalsIgnoreCase("Positivo")) positivos++;
                else if (sentimiento.equalsIgnoreCase("Negativo")) negativos++;
                else neutrales++;
            }

            public String getNombreProducto() { return nombreProducto; }
            public int getTotal() { return total; }
            public int getPositivos() { return positivos; }
            public int getNegativos() { return negativos; }
            public int getNeutrales() { return neutrales; }
        }
    }
