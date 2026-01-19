package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolID;
    @Column(name = "nombre_rol")
    private String nombreRol;
    @ManyToMany(mappedBy = "rol")
     private List<User> user;
}
