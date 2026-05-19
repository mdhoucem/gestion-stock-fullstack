package com.example.gestionstock.services.impl;

import com.example.gestionstock.dto.CategorieDto;
import com.example.gestionstock.entities.Categorie;
import com.example.gestionstock.exception.GestionStockException;
import com.example.gestionstock.repositories.ArticleRepository;
import com.example.gestionstock.repositories.CategorieRepository;
import com.example.gestionstock.services.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CategorieDto save(CategorieDto dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new GestionStockException("Le nom de la catégorie est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (dto.getId() == null && categorieRepository.existsByNom(dto.getNom())) {
            throw new GestionStockException("Une catégorie avec ce nom existe déjà", HttpStatus.CONFLICT);
        }
        Categorie categorie = Categorie.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .build();
        return toDto(categorieRepository.save(categorie));
    }

    @Override
    @Transactional(readOnly = true)
    public CategorieDto findById(Long id) {
        return categorieRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new GestionStockException("Catégorie non trouvée avec l'id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> findAll() {
        return categorieRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Catégorie non trouvée avec l'id: " + id, HttpStatus.NOT_FOUND));

        List<?> articles = articleRepository.findByCategorieId(id);
        if (!articles.isEmpty()) {
            throw new GestionStockException(
                    "Impossible de supprimer la catégorie: elle contient des articles",
                    HttpStatus.BAD_REQUEST
            );
        }
        categorieRepository.delete(categorie);
    }

    private CategorieDto toDto(Categorie categorie) {
        return CategorieDto.builder()
                .id(categorie.getId())
                .nom(categorie.getNom())
                .description(categorie.getDescription())
                .build();
    }
}
