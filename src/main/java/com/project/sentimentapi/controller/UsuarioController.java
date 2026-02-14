package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuario")
public class UsuarioController {
    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        try {
            userService.registrarUsuario(userDtoRegistro);
            return ResponseEntity.ok(java.util.Map.of("message", "Usuario registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT)
                    .body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        return userService.login(userDtoRegistro)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}