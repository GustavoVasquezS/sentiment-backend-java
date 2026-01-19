package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.UserDtoLogin;
import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.RolRepository;
import com.project.sentimentapi.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    public void registrarUsuario(UserDtoRegistro userDtoRegistro) {
        Optional<Rol> rol = rolrepository.findById(2);
        List<Rol> roles = new ArrayList<>();
        roles.add(rol.get());

        String claveHasheada = BCrypt.hashpw(userDtoRegistro.getContraseña(), BCrypt.gensalt());

        // 4. Crea y guarda el usuario
        User nuevoUsuario = new User(
                userDtoRegistro.getNombre(),
                userDtoRegistro.getApellido(),
                claveHasheada,
                userDtoRegistro.getCorreo(),
                roles
        );

        userRepository.save(nuevoUsuario);
    }
    public Optional<UserDtoLogin> login(String correo, String contraseña) {
        List<User> listaDeUsuarios = userRepository.findAll();
        for (User datos : listaDeUsuarios) {
            if (datos.getCorreo().equals(correo)) {
                if (BCrypt.checkpw(contraseña, datos.getContraseña())) {
                    System.out.println("Ingreso Exitoso!");
                    return Optional.of(new UserDtoLogin(datos.getUsuarioID(), datos.getNombre(), datos.getApellido(), datos.getCorreo()));
                }
            }
        }
        return Optional.empty();
    }
}