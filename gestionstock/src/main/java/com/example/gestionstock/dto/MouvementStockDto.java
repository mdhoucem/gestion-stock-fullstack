package com.example.gestionstock.dto;

import com.example.gestionstock.entities.TypeMouvement;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStockDto {
    private Long id;
    private Long articleId;
    private String articleNom;
    private Integer quantite;
    private TypeMouvement typeMouvement;
    private LocalDateTime dateMouvement;
    private String motif;
}
