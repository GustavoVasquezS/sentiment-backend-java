package com.project.sentimentapi.controller;
import com.project.sentimentapi.dto.ProductoDto;
import com.project.sentimentapi.dto.ProductoRequestDto;
import com.project.sentimentapi.service.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/producto")
public class ProductoController {
    @Autowired
    private ProductoService productoService;
    @PostMapping
    public ResponseEntity<?> crearProducto(
            HttpServletRequest request,
            @RequestBody ProductoRequestDto productoRequest
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            ProductoDto producto = productoService.crearProducto(productoRequest, usuarioId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerProductos(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        List<ProductoDto> productos = productoService.obtenerProductosPorUsuario(usuarioId);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> obtenerProductosPorCategoria(
            HttpServletRequest request,
            @PathVariable Integer categoriaId
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            List<ProductoDto> productos = productoService.obtenerProductosPorCategoria(categoriaId, usuarioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<?> obtenerProductoPorId(
            HttpServletRequest request,
            @PathVariable Integer productoId
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            ProductoDto producto = productoService.obtenerProductoPorId(productoId, usuarioId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}