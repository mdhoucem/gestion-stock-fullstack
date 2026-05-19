package com.example.gestionstock.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommandeFournisseurDto {
    private Long id;
    private Long articleId;
    private String articleNom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
