package com.example.gestionstock.repositories;

import com.example.gestionstock.entities.MouvementStock;
import com.example.gestionstock.entities.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    List<MouvementStock> findByArticleIdOrderByDateMouvementDesc(Long articleId);

    @Query("SELECT COALESCE(SUM(CASE " +
           "WHEN m.typeMouvement IN ('ENTREE', 'CORRECTION_POSITIVE') THEN m.quantite " +
           "WHEN m.typeMouvement IN ('SORTIE', 'CORRECTION_NEGATIVE') THEN -m.quantite " +
           "ELSE 0 END), 0) FROM MouvementStock m WHERE m.article.id = :articleId")
    Integer calculerStockReel(@Param("articleId") Long articleId);
}
