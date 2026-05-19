package com.example.gestionstock.services.impl;

import com.example.gestionstock.dto.CommandeClientDto;
import com.example.gestionstock.dto.LigneCommandeClientDto;
import com.example.gestionstock.entities.*;
import com.example.gestionstock.exception.GestionStockException;
import com.example.gestionstock.repositories.ArticleRepository;
import com.example.gestionstock.repositories.CommandeClientRepository;
import com.example.gestionstock.repositories.LigneCommandeClientRepository;
import com.example.gestionstock.services.CommandeClientService;
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
public class CommandeClientServiceImpl implements CommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommandeClientDto save(CommandeClientDto dto) {
        if (dto.getNomClient() == null || dto.getNomClient().isBlank()) {
            throw new GestionStockException("Le nom du client est obligatoire", HttpStatus.BAD_REQUEST);
        }

        CommandeClient commande = CommandeClient.builder()
                .id(dto.getId())
                .nomClient(dto.getNomClient())
                .emailClient(dto.getEmailClient())
                .dateCommande(dto.getDateCommande() != null ? dto.getDateCommande() : LocalDate.now())
                .etatCommande(dto.getEtatCommande() != null ? dto.getEtatCommande() : EtatCommande.EN_COURS_DE_PREPARATION)
                .lignesCommande(new ArrayList<>())
                .build();

        CommandeClient saved = commandeClientRepository.save(commande);

        if (dto.getLignesCommande() != null) {
            for (LigneCommandeClientDto ligneDto : dto.getLignesCommande()) {
                Article article = articleRepository.findById(ligneDto.getArticleId())
                        .orElseThrow(() -> new GestionStockException("Article non trouvé: " + ligneDto.getArticleId(), HttpStatus.NOT_FOUND));

                LigneCommandeClient ligne = LigneCommandeClient.builder()
                        .commandeClient(saved)
                        .article(article)
                        .quantite(ligneDto.getQuantite())
                        .prixUnitaire(ligneDto.getPrixUnitaire() != null ? ligneDto.getPrixUnitaire() : article.getPrixUnitaire())
                        .build();
                ligneCommandeClientRepository.save(ligne);
            }
        }

        return toDto(commandeClientRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeClientDto findById(Long id) {
        return commandeClientRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeClientDto> findAll() {
        return commandeClientRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeClientDto updateEtat(Long id, EtatCommande etat) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + id, HttpStatus.NOT_FOUND));
        commande.setEtatCommande(etat);
        return toDto(commandeClientRepository.save(commande));
    }

    @Override
    public CommandeClientDto updateClient(Long id, String nomClient, String emailClient) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + id, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        commande.setNomClient(nomClient);
        commande.setEmailClient(emailClient);
        return toDto(commandeClientRepository.save(commande));
    }

    @Override
    public CommandeClientDto updateQuantiteLigne(Long commandeId, Long ligneId, Integer quantite) {
        if (quantite <= 0) {
            throw new GestionStockException("La quantité doit être > 0", HttpStatus.BAD_REQUEST);
        }
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + commandeId, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        LigneCommandeClient ligne = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new GestionStockException("Ligne de commande non trouvée: " + ligneId, HttpStatus.NOT_FOUND));

        ligne.setQuantite(quantite);
        ligneCommandeClientRepository.save(ligne);
        return toDto(commandeClientRepository.findById(commandeId).orElseThrow());
    }

    @Override
    public CommandeClientDto supprimerLigne(Long commandeId, Long ligneId) {
        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + commandeId, HttpStatus.NOT_FOUND));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de modifier une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }

        LigneCommandeClient ligne = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new GestionStockException("Ligne de commande non trouvée: " + ligneId, HttpStatus.NOT_FOUND));

        ligneCommandeClientRepository.delete(ligne);
        return toDto(commandeClientRepository.findById(commandeId).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LigneCommandeClientDto> findLignesByCommande(Long commandeId) {
        commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + commandeId, HttpStatus.NOT_FOUND));
        return ligneCommandeClientRepository.findByCommandeClientId(commandeId).stream()
                .map(this::ligneToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new GestionStockException("Commande client non trouvée: " + id, HttpStatus.NOT_FOUND));
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new GestionStockException("Impossible de supprimer une commande déjà livrée", HttpStatus.BAD_REQUEST);
        }
        commandeClientRepository.delete(commande);
    }

    private CommandeClientDto toDto(CommandeClient c) {
        List<LigneCommandeClientDto> lignes = ligneCommandeClientRepository
                .findByCommandeClientId(c.getId()).stream()
                .map(this::ligneToDto)
                .collect(Collectors.toList());

        return CommandeClientDto.builder()
                .id(c.getId())
                .nomClient(c.getNomClient())
                .emailClient(c.getEmailClient())
                .dateCommande(c.getDateCommande())
                .etatCommande(c.getEtatCommande())
                .lignesCommande(lignes)
                .build();
    }

    private LigneCommandeClientDto ligneToDto(LigneCommandeClient l) {
        return LigneCommandeClientDto.builder()
                .id(l.getId())
                .articleId(l.getArticle().getId())
                .articleNom(l.getArticle().getNom())
                .quantite(l.getQuantite())
                .prixUnitaire(l.getPrixUnitaire())
                .build();
    }
}
