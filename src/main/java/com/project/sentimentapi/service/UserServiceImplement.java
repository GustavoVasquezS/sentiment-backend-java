package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.event.UserRegisteredEvent;
import com.project.sentimentapi.repository.RolRepository;
import com.project.sentimentapi.repository.UserRepository;
import com.project.sentimentapi.security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ✅ NUEVA DEPENDENCIA: Para publicar eventos
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional // ✅ IMPORTANTE: Transaccionalidad
    public void registrarUsuario(UserDtoRegistro userDtoRegistro) {
        System.out.println("====================================");
        System.out.println("📝 REGISTRANDO USUARIO: " + userDtoRegistro.getCorreo());

        // ✅ VALIDACIÓN 1: Verificar que el correo no exista
        if (userRepository.findByCorreo(userDtoRegistro.getCorreo()).isPresent()) {
            System.err.println("❌ ERROR: El correo ya está registrado");
            throw new RuntimeException("El correo ya está registrado");
        }

        // ✅ VALIDACIÓN 2: Verificar que el rol exista
        Optional<Rol> rol = rolrepository.findById(2);
        if (rol.isEmpty()) {
            System.err.println("❌ ERROR: Rol no encontrado");
            throw new RuntimeException("Rol no encontrado");
        }

        List<Rol> roles = new ArrayList<>();
        roles.add(rol.get());

        // ✅ Encriptar contraseña
        String claveHasheada = BCrypt.hashpw(userDtoRegistro.getContraseña(), BCrypt.gensalt());

        // ✅ Crear entidad User
        User nuevoUsuario = new User(
                userDtoRegistro.getNombre(),
                userDtoRegistro.getApellido(),
                claveHasheada,
                userDtoRegistro.getCorreo(),
                roles
        );

        // ✅ PASO 1: GUARDAR USUARIO (esto genera el ID)
        User usuarioGuardado = userRepository.save(nuevoUsuario);
        System.out.println("✅ Usuario guardado con ID: " + usuarioGuardado.getUsuarioID());

        // ✅ PASO 2: PUBLICAR EVENTO (el listener creará las categorías automáticamente)
        System.out.println("📢 Publicando evento UserRegisteredEvent...");
        eventPublisher.publishEvent(new UserRegisteredEvent(this, usuarioGuardado));

        System.out.println("✅ REGISTRO COMPLETADO");
        System.out.println("====================================");
    }

    @Override
    public Optional<LoginResponseDto> login(UserDtoRegistro userDtoRegistro) {
        System.out.println("🔐 Intentando login para: " + userDtoRegistro.getCorreo());

        Optional<User> usuarioOpt = userRepository.findByCorreo(userDtoRegistro.getCorreo());

        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();

            // ✅ Verificar contraseña
            if (BCrypt.checkpw(userDtoRegistro.getContraseña(), usuario.getContraseña())) {
                // ✅ Generar token JWT
                String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getUsuarioID());

                System.out.println("✅ Login exitoso - Token generado para: " + usuario.getCorreo());

                return Optional.of(new LoginResponseDto(
                        usuario.getUsuarioID(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        token
                ));
            } else {
                System.err.println("❌ Contraseña incorrecta para: " + userDtoRegistro.getCorreo());
            }
        } else {
            System.err.println("❌ Usuario no encontrado: " + userDtoRegistro.getCorreo());
        }

        return Optional.empty();
    }
}