package com.example.consumerservice.controller;

import com.example.consumerservice.dto.CategoryDTO;
import com.example.consumerservice.dto.ProductDTO;
import com.example.consumerservice.exception.InvalidRequestException;
import com.example.consumerservice.service.CategoryService;
import com.example.consumerservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consumer/")
public class ConsumerController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public ConsumerController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(name = "min_price", required = false) Integer minPrice,
            @RequestParam(name = "max_price", required = false) Integer maxPrice,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(value = "page", required = false) Integer pageNo,
            @RequestParam(value = "size", required = false) Integer pageSize
    ) {
        Page<ProductDTO> response = productService.findAll(Optional.ofNullable(minPrice), Optional.ofNullable(maxPrice),
                Optional.ofNullable(category), Optional.ofNullable(pageNo), Optional.ofNullable(pageSize));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id)); //200
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> getProductByName(@RequestParam String name) {
        List<ProductDTO> products = productService.findByName(name);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/products")
    public ResponseEntity<Void> createProduct(@RequestBody @Valid ProductDTO product,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult); //400
        }
        ProductDTO saved = productService.save(product);
        URI location = URI.create("/consumer/products/" + saved.getId());
        return ResponseEntity.created(location).build(); //201
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id,
                                              @RequestBody @Valid ProductDTO product,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult); //400
        }
        productService.update(product, id);
        return ResponseEntity.accepted().build(); //202
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build(); //204
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll()); //200

    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id)); //200
    }
}
