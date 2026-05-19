package com.example.gestionstock.repositories;

import com.example.gestionstock.entities.LigneCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LigneCommandeFournisseurRepository extends JpaRepository<LigneCommandeFournisseur, Long> {
    List<LigneCommandeFournisseur> findByCommandeFournisseurId(Long commandeFournisseurId);
    boolean existsByArticleId(Long articleId);
}
