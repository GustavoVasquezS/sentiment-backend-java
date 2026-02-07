package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("usuario")
public class UsuarioController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        // Validaciones basicas
        if (userDtoRegistro.getCorreo() == null || userDtoRegistro.getCorreo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
        }
        if (userDtoRegistro.getContrasena() == null || userDtoRegistro.getContrasena().length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contrasena debe tener al menos 4 caracteres"));
        }
        if (userDtoRegistro.getNombre() == null || userDtoRegistro.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        if (userDtoRegistro.getApellido() == null || userDtoRegistro.getApellido().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El apellido es obligatorio"));
        }

        try {
            userService.registrarUsuario(userDtoRegistro);
            return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("correo ya esta registrado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", msg));
            }
            System.err.println("Error en registro: " + msg);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al registrar usuario: " + (msg != null ? msg : "desconocido")));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        return userService.login(userDtoRegistro)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas")));
    }
}