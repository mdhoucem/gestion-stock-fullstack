package com.example.gestionstock.controllers;

import com.example.gestionstock.dto.CategorieDto;
import com.example.gestionstock.services.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    public ResponseEntity<CategorieDto> save(@RequestBody CategorieDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieDto> update(@PathVariable Long id, @RequestBody CategorieDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(categorieService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategorieDto>> findAll() {
        return ResponseEntity.ok(categorieService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
