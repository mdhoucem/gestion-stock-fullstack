package com.example.gestionstock.controllers;

import com.example.gestionstock.dto.ArticleDto;
import com.example.gestionstock.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleDto> save(@RequestBody ArticleDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> update(@PathVariable Long id, @RequestBody ArticleDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(articleService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> findAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ArticleDto>> findByCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(articleService.findByCategorie(categorieId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
