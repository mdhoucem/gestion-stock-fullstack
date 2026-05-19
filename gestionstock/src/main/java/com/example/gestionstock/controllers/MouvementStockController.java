package com.example.gestionstock.controllers;

import com.example.gestionstock.dto.MouvementStockDto;
import com.example.gestionstock.services.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    // Entree de stock (commande fournisseur)
    @PostMapping("/entree/{articleId}")
    public ResponseEntity<MouvementStockDto> entreeStock(
            @PathVariable Long articleId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) String motif) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mouvementStockService.entreeStock(articleId, quantite, motif));
    }

    // Sortie de stock (vente / commande client)
    @PostMapping("/sortie/{articleId}")
    public ResponseEntity<MouvementStockDto> sortieStock(
            @PathVariable Long articleId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) String motif) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mouvementStockService.sortieStock(articleId, quantite, motif));
    }

    // Correction positive
    @PostMapping("/correction-positive/{articleId}")
    public ResponseEntity<MouvementStockDto> correctionPositive(
            @PathVariable Long articleId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) String motif) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mouvementStockService.correctionPositive(articleId, quantite, motif));
    }

    // Correction négative
    @PostMapping("/correction-negative/{articleId}")
    public ResponseEntity<MouvementStockDto> correctionNegative(
            @PathVariable Long articleId,
            @RequestParam Integer quantite,
            @RequestParam(required = false) String motif) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mouvementStockService.correctionNegative(articleId, quantite, motif));
    }

    // Stock réel d'un article
    @GetMapping("/stock-reel/{articleId}")
    public ResponseEntity<Integer> getStockReel(@PathVariable Long articleId) {
        return ResponseEntity.ok(mouvementStockService.calculerStockReel(articleId));
    }

    // Historique des mouvements d'un article
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<MouvementStockDto>> findByArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(mouvementStockService.findByArticle(articleId));
    }
}
