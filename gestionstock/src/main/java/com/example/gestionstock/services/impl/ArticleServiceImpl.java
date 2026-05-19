package com.example.gestionstock.services.impl;

import com.example.gestionstock.dto.ArticleDto;
import com.example.gestionstock.entities.Article;
import com.example.gestionstock.entities.Categorie;
import com.example.gestionstock.exception.GestionStockException;
import com.example.gestionstock.repositories.ArticleRepository;
import com.example.gestionstock.repositories.CategorieRepository;
import com.example.gestionstock.repositories.LigneCommandeClientRepository;
import com.example.gestionstock.repositories.LigneCommandeFournisseurRepository;
import com.example.gestionstock.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;

    @Override
    public ArticleDto save(ArticleDto dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new GestionStockException("Le nom de l'article est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (dto.getPrixUnitaire() == null) {
            throw new GestionStockException("Le prix unitaire est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (dto.getQuantiteEnStock() == null || dto.getQuantiteEnStock() < 0) {
            throw new GestionStockException("La quantité en stock doit être >= 0", HttpStatus.BAD_REQUEST);
        }

        Categorie categorie = null;
        if (dto.getCategorieId() != null) {
            categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new GestionStockException("Catégorie non trouvée avec l'id: " + dto.getCategorieId(), HttpStatus.NOT_FOUND));
        }

        Article article = Article.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .prixUnitaire(dto.getPrixUnitaire())
                .quantiteEnStock(dto.getQuantiteEnStock())
                .categorie(categorie)
                .build();

        return toDto(articleRepository.save(article));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto findById(Long id) {
        return articleRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new GestionStockException("Article non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> findAll() {
        return articleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> findByCategorie(Long categorieId) {
        categorieRepository.findById(categorieId)
                .orElseThrow(() -> new GestionStockException("Catégorie non trouvée avec l'id: " + categorieId, HttpStatus.NOT_FOUND));
        return articleRepository.findByCategorieId(categorieId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Article non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND));

        if (ligneCommandeClientRepository.existsByArticleId(id)) {
            throw new GestionStockException(
                    "Impossible de supprimer l'article: il est utilisé dans des commandes clients",
                    HttpStatus.BAD_REQUEST
            );
        }
        if (ligneCommandeFournisseurRepository.existsByArticleId(id)) {
            throw new GestionStockException(
                    "Impossible de supprimer l'article: il est utilisé dans des commandes fournisseurs",
                    HttpStatus.BAD_REQUEST
            );
        }
        articleRepository.delete(article);
    }

    private ArticleDto toDto(Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .nom(article.getNom())
                .description(article.getDescription())
                .prixUnitaire(article.getPrixUnitaire())
                .quantiteEnStock(article.getQuantiteEnStock())
                .categorieId(article.getCategorie() != null ? article.getCategorie().getId() : null)
                .categorieNom(article.getCategorie() != null ? article.getCategorie().getNom() : null)
                .build();
    }
}
