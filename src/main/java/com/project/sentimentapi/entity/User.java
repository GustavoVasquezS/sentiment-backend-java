package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioID;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(unique = true, nullable = false)
    private String correo;
    @Column(nullable = false)
    private String contraseña;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "User_rol",joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "rol_Id",referencedColumnName = "rol_id"))
    private List<Rol> rol;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "fk")
    private List<Interaccion> interacciones;
    public User(String nombre, String apellido,String contraseña, String correo, List<Rol> rol){
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
        }
    }

