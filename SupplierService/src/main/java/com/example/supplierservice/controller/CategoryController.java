package com.example.supplierservice.controller;

import com.example.supplierservice.model.Category;
import com.example.supplierservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/supplier/categories")
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        categoryService.save(category);
        URI location = URI.create("/supplier/categories/" + category.getId());
        return ResponseEntity.created(location).build(); //201
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll()); //200
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id)); //200
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        categoryService.update(category, id);
        return ResponseEntity.accepted().build(); //202
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build(); //204
    }
}
