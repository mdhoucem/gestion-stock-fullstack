package com.example.gestionstock.services;

import com.example.gestionstock.dto.ArticleDto;
import java.util.List;

public interface ArticleService {
    ArticleDto save(ArticleDto dto);
    ArticleDto findById(Long id);
    List<ArticleDto> findAll();
    List<ArticleDto> findByCategorie(Long categorieId);
    void delete(Long id);
}
