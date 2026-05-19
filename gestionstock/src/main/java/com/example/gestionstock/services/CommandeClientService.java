package com.example.gestionstock.services;

import com.example.gestionstock.dto.CommandeClientDto;
import com.example.gestionstock.dto.LigneCommandeClientDto;
import com.example.gestionstock.entities.EtatCommande;
import java.util.List;

public interface CommandeClientService {
    CommandeClientDto save(CommandeClientDto dto);
    CommandeClientDto findById(Long id);
    List<CommandeClientDto> findAll();
    CommandeClientDto updateEtat(Long id, EtatCommande etat);
    CommandeClientDto updateClient(Long id, String nomClient, String emailClient);
    CommandeClientDto updateQuantiteLigne(Long commandeId, Long ligneId, Integer quantite);
    CommandeClientDto supprimerLigne(Long commandeId, Long ligneId);
    List<LigneCommandeClientDto> findLignesByCommande(Long commandeId);
    void delete(Long id);
}
