package com.example.gestionstock.services;

import com.example.gestionstock.dto.CommandeFournisseurDto;
import com.example.gestionstock.dto.LigneCommandeFournisseurDto;
import com.example.gestionstock.entities.EtatCommande;
import java.util.List;

public interface CommandeFournisseurService {
    CommandeFournisseurDto save(CommandeFournisseurDto dto);
    CommandeFournisseurDto findById(Long id);
    List<CommandeFournisseurDto> findAll();
    CommandeFournisseurDto updateEtat(Long id, EtatCommande etat);
    CommandeFournisseurDto updateFournisseur(Long id, String nomFournisseur, String emailFournisseur);
    CommandeFournisseurDto updateQuantiteLigne(Long commandeId, Long ligneId, Integer quantite);
    CommandeFournisseurDto supprimerLigne(Long commandeId, Long ligneId);
    List<LigneCommandeFournisseurDto> findLignesByCommande(Long commandeId);
    void delete(Long id);
}
