package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.Interaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteraccionRepository extends JpaRepository<Interaccion,Integer> {
}
