package com.example.gestionstock.repositories;

import com.example.gestionstock.entities.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, Long> {
}
