package com.project.sentimentapi.event;

import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRegistrationListener {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserRegistered(UserRegisteredEvent event) {
        User usuario = event.getUsuario();

        System.out.println("====================================");
        System.out.println("EVENTO: Creando categorias para usuario: " + usuario.getCorreo());
        System.out.println("Usuario ID: " + usuario.getUsuarioID());

        try {
            List<Categoria> categoriasDefault = crearCategoriasDefault(usuario);
            categoriaRepository.saveAll(categoriasDefault);

            System.out.println("EXITO: " + categoriasDefault.size() + " categorias creadas");
            System.out.println("====================================");
        } catch (Exception e) {
            System.err.println("ERROR al crear categorias: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Categoria> crearCategoriasDefault(User usuario) {
        List<Categoria> categorias = new ArrayList<>();

        categorias.add(new Categoria("Electronica", "Productos electronicos, smartphones, computadoras", usuario));
        categorias.add(new Categoria("Ropa y Moda", "Vestimenta, calzado, accesorios", usuario));
        categorias.add(new Categoria("Alimentos y Bebidas", "Productos comestibles, bebidas, snacks", usuario));
        categorias.add(new Categoria("Hogar y Decoracion", "Muebles, decoracion, articulos para el hogar", usuario));
        categorias.add(new Categoria("Belleza y Cuidado Personal", "Cosmeticos, cuidado de la piel", usuario));
        categorias.add(new Categoria("Entretenimiento", "Videojuegos, libros, peliculas, musica", usuario));
        categorias.add(new Categoria("Deportes y Fitness", "Equipamiento deportivo, ropa deportiva", usuario));
        categorias.add(new Categoria("Servicios", "Servicios profesionales, delivery, suscripciones", usuario));
        categorias.add(new Categoria("Automotriz", "Vehiculos, repuestos, accesorios", usuario));
        categorias.add(new Categoria("Educacion", "Cursos, capacitaciones, material educativo", usuario));
        categorias.add(new Categoria("Salud y Bienestar", "Productos medicos, suplementos, vitaminas", usuario));
        categorias.add(new Categoria("Ninos y Bebes", "Productos para bebes, juguetes, ropa infantil", usuario));

        return categorias;
    }
}