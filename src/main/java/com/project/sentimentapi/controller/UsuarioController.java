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

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoRegistro userDtoRegistro) {
        return userService.login(userDtoRegistro)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}