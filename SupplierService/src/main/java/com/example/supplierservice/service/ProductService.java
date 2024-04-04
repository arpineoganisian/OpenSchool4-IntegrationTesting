package com.example.supplierservice.service;

import com.example.supplierservice.model.Category;
import com.example.supplierservice.model.Product;
import com.example.supplierservice.repository.CategoryRepository;
import com.example.supplierservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Product> findAll(Integer minPrice, Integer maxPrice, String category,
                                 Integer pageNo, Integer pageSize) {

        Pageable paging = (pageNo == null || pageSize == null)
                ? Pageable.unpaged() : PageRequest.of(pageNo, pageSize);
        Specification<Product> specification = Specification.where(null);

        if (minPrice != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (category != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("category").get("name")),
                            category.toLowerCase())
            );
        }

        return productRepository.findAll(specification, paging);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product save(Product product) {
        Category category = product.getCategory();
        if (category != null && categoryRepository.findById(category.getId()).isEmpty())
            return null;
        return productRepository.save(product);
    }

    @Transactional
    public void update(Product product, Long id) {
        product.setId(id);
        productRepository.save(product);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        productRepository.deleteAllAndResetId();
    }

}
