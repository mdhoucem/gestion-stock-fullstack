package com.example.gestionstock.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixUnitaire;
    private Integer quantiteEnStock;
    private Long categorieId;
    private String categorieNom;
}
