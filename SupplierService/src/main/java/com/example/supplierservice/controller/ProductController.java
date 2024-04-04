package com.example.supplierservice.controller;

import com.example.supplierservice.model.Product;
import com.example.supplierservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supplier/products")
public class ProductController {
    ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product == null) {
            return ResponseEntity.badRequest().build(); // 400
        }
        Product saved = productService.save(product);
//        if (saved == null) {
//            return ResponseEntity.badRequest().build(); // 400
//        }
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); //201
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(name = "min_price", required = false) Integer minPrice,
            @RequestParam(name = "max_price", required = false) Integer maxPrice,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(value = "page", required = false) Integer pageNo,
            @RequestParam(value = "size", required = false) Integer pageSize) {
        return ResponseEntity.ok(productService.findAll(minPrice, maxPrice, category, pageNo, pageSize)); //200
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id)); //200
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productService.existsById(id) || product == null) {
            return ResponseEntity.badRequest().build(); // 400
        }
        productService.update(product, id);
        return ResponseEntity.accepted().build(); //202
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.badRequest().build(); // 400
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build(); //204
    }
}
