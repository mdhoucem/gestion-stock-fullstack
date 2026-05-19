package com.example.gestionstock.dto;

import com.example.gestionstock.entities.EtatCommande;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeFournisseurDto {
    private Long id;
    private String nomFournisseur;
    private String emailFournisseur;
    private LocalDate dateCommande;
    private EtatCommande etatCommande;
    private List<LigneCommandeFournisseurDto> lignesCommande;
}
