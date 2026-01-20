package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.RolRepository;
import com.project.sentimentapi.repository.UserRepository;
import com.project.sentimentapi.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplement implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolRepository rolrepository;

    @Autowired
    JwtUtil jwtUtil;

    public void registrarUsuario(UserDtoRegistro userDtoRegistro) {
        Optional<Rol> rol = rolrepository.findById(2);
        List<Rol> roles = new ArrayList<>();
        roles.add(rol.get());

        String claveHasheada = BCrypt.hashpw(userDtoRegistro.getContraseña(), BCrypt.gensalt());

        User nuevoUsuario = new User(
                userDtoRegistro.getNombre(),
                userDtoRegistro.getApellido(),
                claveHasheada,
                userDtoRegistro.getCorreo(),
                roles
        );

        userRepository.save(nuevoUsuario);
    }

    public Optional<LoginResponseDto> login(UserDtoRegistro userDtoRegistro) {
        Optional<User> usuarioOpt = userRepository.findByCorreo(userDtoRegistro.getCorreo());

        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();

            if (BCrypt.checkpw(userDtoRegistro.getContraseña(), usuario.getContraseña())) {
                // Generar token JWT
                String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getUsuarioID());

                return Optional.of(new LoginResponseDto(
                        usuario.getUsuarioID(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        token
                ));
            }
        }

        return Optional.empty();
    }
}