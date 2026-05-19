package com.example.gestionstock.services;

import com.example.gestionstock.dto.CategorieDto;
import java.util.List;

public interface CategorieService {
    CategorieDto save(CategorieDto dto);
    CategorieDto findById(Long id);
    List<CategorieDto> findAll();
    void delete(Long id);
}
