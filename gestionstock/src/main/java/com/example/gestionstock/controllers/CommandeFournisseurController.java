package com.example.gestionstock.controllers;

import com.example.gestionstock.dto.CommandeFournisseurDto;
import com.example.gestionstock.dto.LigneCommandeFournisseurDto;
import com.example.gestionstock.entities.EtatCommande;
import com.example.gestionstock.services.CommandeFournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes-fournisseurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    @PostMapping
    public ResponseEntity<CommandeFournisseurDto> save(@RequestBody CommandeFournisseurDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeFournisseurService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeFournisseurDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeFournisseurService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CommandeFournisseurDto>> findAll() {
        return ResponseEntity.ok(commandeFournisseurService.findAll());
    }

    // Modifier l'état de la commande
    @PatchMapping("/{id}/etat")
    public ResponseEntity<CommandeFournisseurDto> updateEtat(
            @PathVariable Long id,
            @RequestParam EtatCommande etat) {
        return ResponseEntity.ok(commandeFournisseurService.updateEtat(id, etat));
    }

    // Modifier le fournisseur
    @PatchMapping("/{id}/fournisseur")
    public ResponseEntity<CommandeFournisseurDto> updateFournisseur(
            @PathVariable Long id,
            @RequestParam String nomFournisseur,
            @RequestParam(required = false) String emailFournisseur) {
        return ResponseEntity.ok(commandeFournisseurService.updateFournisseur(id, nomFournisseur, emailFournisseur));
    }

    // Modifier la quantité d'une ligne
    @PatchMapping("/{commandeId}/lignes/{ligneId}/quantite")
    public ResponseEntity<CommandeFournisseurDto> updateQuantiteLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite) {
        return ResponseEntity.ok(commandeFournisseurService.updateQuantiteLigne(commandeId, ligneId, quantite));
    }

    // Supprimer une ligne de commande
    @DeleteMapping("/{commandeId}/lignes/{ligneId}")
    public ResponseEntity<CommandeFournisseurDto> supprimerLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId) {
        return ResponseEntity.ok(commandeFournisseurService.supprimerLigne(commandeId, ligneId));
    }

    // Obtenir les lignes d'une commande
    @GetMapping("/{commandeId}/lignes")
    public ResponseEntity<List<LigneCommandeFournisseurDto>> findLignes(@PathVariable Long commandeId) {
        return ResponseEntity.ok(commandeFournisseurService.findLignesByCommande(commandeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeFournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
