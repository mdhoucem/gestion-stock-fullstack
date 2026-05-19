package com.example.gestionstock.controllers;

import com.example.gestionstock.dto.CommandeClientDto;
import com.example.gestionstock.dto.LigneCommandeClientDto;
import com.example.gestionstock.entities.EtatCommande;
import com.example.gestionstock.services.CommandeClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes-clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    @PostMapping
    public ResponseEntity<CommandeClientDto> save(@RequestBody CommandeClientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeClientService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeClientDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeClientService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CommandeClientDto>> findAll() {
        return ResponseEntity.ok(commandeClientService.findAll());
    }

    // Modifier l'état de la commande
    @PatchMapping("/{id}/etat")
    public ResponseEntity<CommandeClientDto> updateEtat(
            @PathVariable Long id,
            @RequestParam EtatCommande etat) {
        return ResponseEntity.ok(commandeClientService.updateEtat(id, etat));
    }

    // Modifier le client
    @PatchMapping("/{id}/client")
    public ResponseEntity<CommandeClientDto> updateClient(
            @PathVariable Long id,
            @RequestParam String nomClient,
            @RequestParam(required = false) String emailClient) {
        return ResponseEntity.ok(commandeClientService.updateClient(id, nomClient, emailClient));
    }

    // Modifier la quantité d'une ligne
    @PatchMapping("/{commandeId}/lignes/{ligneId}/quantite")
    public ResponseEntity<CommandeClientDto> updateQuantiteLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite) {
        return ResponseEntity.ok(commandeClientService.updateQuantiteLigne(commandeId, ligneId, quantite));
    }

    // Supprimer une ligne de commande
    @DeleteMapping("/{commandeId}/lignes/{ligneId}")
    public ResponseEntity<CommandeClientDto> supprimerLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId) {
        return ResponseEntity.ok(commandeClientService.supprimerLigne(commandeId, ligneId));
    }

    // Obtenir les lignes d'une commande
    @GetMapping("/{commandeId}/lignes")
    public ResponseEntity<List<LigneCommandeClientDto>> findLignes(@PathVariable Long commandeId) {
        return ResponseEntity.ok(commandeClientService.findLignesByCommande(commandeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeClientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
