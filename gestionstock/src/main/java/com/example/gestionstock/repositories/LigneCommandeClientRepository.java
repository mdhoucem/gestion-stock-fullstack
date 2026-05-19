package com.example.gestionstock.repositories;

import com.example.gestionstock.entities.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Long> {
    List<LigneCommandeClient> findByCommandeClientId(Long commandeClientId);
    boolean existsByArticleId(Long articleId);
}
