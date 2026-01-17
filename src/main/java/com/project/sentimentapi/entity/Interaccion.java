package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "interaccion")
public class Interaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInteracion;
    private List<String> comentario;
    @Column(name = "fecha_creacion", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime localDateTime;
    private List<String> reseña;
    @ManyToOne
    private User fk;
}
