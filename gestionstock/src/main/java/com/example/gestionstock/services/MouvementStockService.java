package com.example.gestionstock.services;

import com.example.gestionstock.dto.MouvementStockDto;
import java.util.List;

public interface MouvementStockService {
    MouvementStockDto entreeStock(Long articleId, Integer quantite, String motif);
    MouvementStockDto sortieStock(Long articleId, Integer quantite, String motif);
    MouvementStockDto correctionPositive(Long articleId, Integer quantite, String motif);
    MouvementStockDto correctionNegative(Long articleId, Integer quantite, String motif);
    Integer calculerStockReel(Long articleId);
    List<MouvementStockDto> findByArticle(Long articleId);
}
