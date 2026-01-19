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
        userService.registrarUsuario(userDtoRegistro);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        if (!userService.login(userDtoRegistro).isEmpty()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
