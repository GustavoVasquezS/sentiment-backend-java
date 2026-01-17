package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.UserDtoLogin;
import com.project.sentimentapi.dto.UserDtoRegistro;
import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.RolRepository;
import com.project.sentimentapi.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplement implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RolRepository rolrepository;

    public void registrarUsuario(UserDtoRegistro userDtoRegistro) {
        System.out.println(userDtoRegistro.getNombre());
        Optional<Rol> rol = rolrepository.findById(2);
        String claveHasheada = BCrypt.hashpw(userDtoRegistro.getContraseña(), BCrypt.gensalt());
        userRepository.save(new User(userDtoRegistro.getNombre(), userDtoRegistro.getApellido(), claveHasheada, userDtoRegistro.getCorreo(), rol.get()));
    }

    public Optional<UserDtoLogin> login(String correo, String contraseña) {
        List<User> listaDeUsuarios = userRepository.findAll();
        for (User datos : listaDeUsuarios) {
            if (datos.getCorreo().equals(correo)) {
                if (BCrypt.checkpw(contraseña, datos.getContraseña())) {
                    System.out.println("Ingreso Exitoso!");
                    return Optional.of(new UserDtoLogin(datos.getUsuarioID(), datos.getNombre(), datos.getApellido(), datos.getCorreo()));
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}