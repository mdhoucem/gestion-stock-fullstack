package com.example.gestionstock.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "commande_client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    private String emailClient;

    @Column(nullable = false)
    private LocalDate dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCommande etatCommande;

    @OneToMany(mappedBy = "commandeClient", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<LigneCommandeClient> lignesCommande;
}
