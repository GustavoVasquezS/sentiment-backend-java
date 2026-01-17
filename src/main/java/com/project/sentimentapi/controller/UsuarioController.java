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
    public ResponseEntity<?> registrarUsuario(@RequestBody UserDtoRegistro userDtoRegistro){
       userService.registrarUsuario(userDtoRegistro);
       return ResponseEntity.ok().build();
    }
    @GetMapping("/{correo}/{contraseña}")
    public ResponseEntity<?> loginUsuario(@PathVariable String correo, @PathVariable String contraseña){
        userService.login(correo,contraseña);
        return ResponseEntity.ok().build();
    }
}
