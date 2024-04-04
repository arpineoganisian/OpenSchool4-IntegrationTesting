package com.example.supplierservice.service;

import com.example.supplierservice.model.Category;
import com.example.supplierservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public void update(Category category, Long id) {
        category.setId(id);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
