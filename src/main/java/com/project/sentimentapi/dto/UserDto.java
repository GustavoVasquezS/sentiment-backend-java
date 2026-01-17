package com.project.sentimentapi.dto;

import com.project.sentimentapi.entity.Interaccion;
import lombok.Data;
import java.util.List;
@Data

public class UserDto {
    private String nombre;
    private String apellido;
    private String correo;
    private List<Interaccion> interaccions;
}
