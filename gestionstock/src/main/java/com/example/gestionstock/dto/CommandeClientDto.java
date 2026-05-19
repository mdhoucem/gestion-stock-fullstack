package com.example.gestionstock.dto;

import com.example.gestionstock.entities.EtatCommande;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeClientDto {
    private Long id;
    private String nomClient;
    private String emailClient;
    private LocalDate dateCommande;
    private EtatCommande etatCommande;
    private List<LigneCommandeClientDto> lignesCommande;
}
