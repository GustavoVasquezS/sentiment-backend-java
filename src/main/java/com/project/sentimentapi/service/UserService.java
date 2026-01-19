package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.UserDto;
import com.project.sentimentapi.dto.UserDtoLogin;
import com.project.sentimentapi.dto.UserDtoRegistro;

import java.util.Optional;

public interface UserService {
    void registrarUsuario(UserDtoRegistro userDtoRegistro);
    Optional<UserDtoLogin> login (UserDtoRegistro userDtoRegistro);
}
