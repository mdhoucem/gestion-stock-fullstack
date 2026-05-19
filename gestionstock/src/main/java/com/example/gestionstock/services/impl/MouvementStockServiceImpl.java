package com.example.gestionstock.services.impl;

import com.example.gestionstock.dto.MouvementStockDto;
import com.example.gestionstock.entities.Article;
import com.example.gestionstock.entities.MouvementStock;
import com.example.gestionstock.entities.TypeMouvement;
import com.example.gestionstock.exception.GestionStockException;
import com.example.gestionstock.repositories.ArticleRepository;
import com.example.gestionstock.repositories.MouvementStockRepository;
import com.example.gestionstock.services.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MouvementStockServiceImpl implements MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final ArticleRepository articleRepository;

    @Override
    public MouvementStockDto entreeStock(Long articleId, Integer quantite, String motif) {
        return creerMouvement(articleId, quantite, TypeMouvement.ENTREE, motif);
    }

    @Override
    public MouvementStockDto sortieStock(Long articleId, Integer quantite, String motif) {
        Integer stockActuel = calculerStockReel(articleId);
        if (stockActuel < quantite) {
            throw new GestionStockException(
                    "Stock insuffisant. Stock actuel: " + stockActuel + ", Quantité demandée: " + quantite,
                    HttpStatus.BAD_REQUEST
            );
        }
        return creerMouvement(articleId, quantite, TypeMouvement.SORTIE, motif);
    }

    @Override
    public MouvementStockDto correctionPositive(Long articleId, Integer quantite, String motif) {
        return creerMouvement(articleId, quantite, TypeMouvement.CORRECTION_POSITIVE, motif);
    }

    @Override
    public MouvementStockDto correctionNegative(Long articleId, Integer quantite, String motif) {
        Integer stockActuel = calculerStockReel(articleId);
        if (stockActuel < quantite) {
            throw new GestionStockException(
                    "Correction impossible. Stock actuel: " + stockActuel + ", Correction demandée: " + quantite,
                    HttpStatus.BAD_REQUEST
            );
        }
        return creerMouvement(articleId, quantite, TypeMouvement.CORRECTION_NEGATIVE, motif);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculerStockReel(Long articleId) {
        articleRepository.findById(articleId)
                .orElseThrow(() -> new GestionStockException("Article non trouvé: " + articleId, HttpStatus.NOT_FOUND));
        return mouvementStockRepository.calculerStockReel(articleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MouvementStockDto> findByArticle(Long articleId) {
        articleRepository.findById(articleId)
                .orElseThrow(() -> new GestionStockException("Article non trouvé: " + articleId, HttpStatus.NOT_FOUND));
        return mouvementStockRepository.findByArticleIdOrderByDateMouvementDesc(articleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MouvementStockDto creerMouvement(Long articleId, Integer quantite, TypeMouvement type, String motif) {
        if (quantite == null || quantite <= 0) {
            throw new GestionStockException("La quantité doit être > 0", HttpStatus.BAD_REQUEST);
        }
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GestionStockException("Article non trouvé: " + articleId, HttpStatus.NOT_FOUND));

        MouvementStock mouvement = MouvementStock.builder()
                .article(article)
                .quantite(quantite)
                .typeMouvement(type)
                .dateMouvement(LocalDateTime.now())
                .motif(motif)
                .build();

        return toDto(mouvementStockRepository.save(mouvement));
    }

    private MouvementStockDto toDto(MouvementStock m) {
        return MouvementStockDto.builder()
                .id(m.getId())
                .articleId(m.getArticle().getId())
                .articleNom(m.getArticle().getNom())
                .quantite(m.getQuantite())
                .typeMouvement(m.getTypeMouvement())
                .dateMouvement(m.getDateMouvement())
                .motif(m.getMotif())
                .build();
    }
}
