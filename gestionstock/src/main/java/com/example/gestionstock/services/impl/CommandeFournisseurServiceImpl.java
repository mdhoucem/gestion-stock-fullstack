package com.example.gestionstock.services.impl;

import com.example.gestionstock.dto.CommandeFournisseurDto;
import com.example.gestionstock.dto.LigneCommandeFournisseurDto;
import com.example.gestionstock.entities.*;
import com.example.gestionstock.exception.GestionStockException;
import com.example.gestionstock.repositories.ArticleRepository;
import com.example.gestionstock.repositories.CommandeFournisseurRepository;
import com.example.gestionstock.repositories.LigneCommandeFournisseurRepository;
import com.example.gestionstock.services.CommandeFournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {

    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommandeFournisseurDto save(CommandeFournisseurDto dto) {
        if (dto.getNomFournisseur() == null || dto.getNomFournisseur().isBlank()) {
            throw new GestionStockException("Le nom du fournisseur est obligatoire", HttpStatus.BAD_REQUEST);
        }

        CommandeFournisseur commande = CommandeFournisseur.builder()
                .id(dto.getId())
                .nomFournisseur(dto.getNomFournisseur())
                .emailFournisseur(dto.getEmailFournisseur())
                .dateCommande(dto.getDateCommande() != null ? dto.getDateCommande() : LocalDate.now())
                .etatCommande(dto.getEtatCommande() != null ? dto.getEtatCommande() : EtatCommande.EN_COURS_DE_PREPARATION)
                .lignesCommande(new ArrayList<>())
                .build();

        CommandeFournisseur saved = commandeFournisseurRepository.save(commande);

        if (dto.getLignesCommande() != null) {
            for (LigneCommandeFournisseurDto ligneDto : dto.getLignesCommande()) {
                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new GestionStockException("Article non trouvé: " + ligneDto.getArticleId(), HttpStatus.NOT_FOUND));

                LigneCommandeFournisseur ligne = LigneCommandeFournisseur.builder()
                        .commandeFournisseur(saved)
                        .article(article)
                        .quantite(ligneDto.getQuantite())
                        .prixUnitaire(ligneDto.getPrixUnitaire() != null ? ligneDto.getPrixUnitaire() : article.getPrixUnitaire())
                        .build();
                ligneCommandeFournisseurRepository.save(ligne);
            }
        }

        return toDto(commandeFournisseurRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeFournisseurDto findById(Long id) {
        return commandeFournisseurRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeFournisseurDto> findAll() {
        return commandeFournisseurRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeFournisseurDto updateEtat(Long id, EtatCommande etat) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + id, HttpStatus.NOT_FOUND));
        commande.setEtatCommande(etat);
        return toDto(commandeFournisseurRepository.save(commande));
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Long id, String nomFournisseur, String emailFournisseur) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + id, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        commande.setNomFournisseur(nomFournisseur);
        commande.setEmailFournisseur(emailFournisseur);
        return toDto(commandeFournisseurRepository.save(commande));
    }

    @Override
    public CommandeFournisseurDto updateQuantiteLigne(Long commandeId, Long ligneId, Integer quantite) {
        if (quantite <= 0) {
            throw new GestionStockException("La quantité doit être > 0", HttpStatus.BAD_REQUEST);
        }
        CommandeFournisseur commande = commandeFournisseurRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + commandeId, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        LigneCommandeFournisseur ligne = ligneCommandeFournisseurRepository.findById(ligneId)
                .orElseThrow(() -> new GestionStockException("Ligne de commande non trouvée: " + ligneId, HttpStatus.NOT_FOUND));

        ligne.setQuantite(quantite);
        ligneCommandeFournisseurRepository.save(ligne);
        return toDto(commandeFournisseurRepository.findById(commandeId).orElseThrow());
    }

    @Override
    public CommandeFournisseurDto supprimerLigne(Long commandeId, Long ligneId) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + commandeId, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        LigneCommandeFournisseur ligne = ligneCommandeFournisseurRepository.findById(ligneId)
                .orElseThrow(() -> new GestionStockException("Ligne de commande non trouvée: " + ligneId, HttpStatus.NOT_FOUND));

        ligneCommandeFournisseurRepository.delete(ligne);
        return toDto(commandeFournisseurRepository.findById(commandeId).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneCommandeFournisseurDto> findLignesByCommande(Long commandeId) {
        commandeFournisseurRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + commandeId, HttpStatus.NOT_FOUND));
        return ligneCommandeFournisseurRepository.findByCommandeFournisseurId(commandeId).stream()
                .map(this::ligneToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande fournisseur non trouvée: " + id, HttpStatus.NOT_FOUND));
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de supprimer une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }
        commandeFournisseurRepository.delete(commande);
    }

    private CommandeFournisseurDto toDto(CommandeFournisseur c) {
        List<LigneCommandeFournisseurDto> lignes = ligneCommandeFournisseurRepository
                .findByCommandeFournisseurId(c.getId()).stream()
                .map(this::ligneToDto)
                .collect(Collectors.toList());

        return CommandeFournisseurDto.builder()
                .id(c.getId())
                .nomFournisseur(c.getNomFournisseur())
                .emailFournisseur(c.getEmailFournisseur())
                .dateCommande(c.getDateCommande())
                .etatCommande(c.getEtatCommande())
                .lignesCommande(lignes)
                .build();
    }

    private LigneCommandeFournisseurDto ligneToDto(LigneCommandeFournisseur l) {
        return LigneCommandeFournisseurDto.builder()
                .id(l.getId())
                .articleId(l.getArticle().getId())
                .articleNom(l.getArticle().getNom())
                .quantite(l.getQuantite())
                .prixUnitaire(l.getPrixUnitaire())
                .build();
    }
}
