package com.example.consumerservice.service;

import com.example.consumerservice.dto.ProductDTO;
import com.example.consumerservice.exception.InvalidRequestException;
import com.example.consumerservice.util.JacksonPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private static final String SUPPLIER_SERVICE_URL = "http://supplier:8080/supplier/products";
//    private static final String SUPPLIER_SERVICE_URL = "http://localhost:8080/supplier/products";

    private final RestTemplate restTemplate;

    @Autowired
    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Page<ProductDTO> findAll(Optional<Integer> minPrice, Optional<Integer> maxPrice, Optional<String> category,
                                    Optional<Integer> pageNo, Optional<Integer> pageSize) {

        if (pageNo.isPresent() != pageSize.isPresent()) // задан один параметр без другого
            throw new InvalidRequestException("Both page and size should be specified or none of them.");

        pageNo.ifPresent(page -> {
            if (page < 0) throw new InvalidRequestException("Page number cannot be negative"); });
        pageSize.ifPresent(size -> {
            if (size <= 0) throw new InvalidRequestException("Page size cannot be less than or equal to zero"); });

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SUPPLIER_SERVICE_URL)
                .queryParamIfPresent("min_price", minPrice)
                .queryParamIfPresent("max_price", maxPrice)
                .queryParamIfPresent("category", category)
                .queryParamIfPresent("page", pageNo)
                .queryParamIfPresent("size", pageSize);

        ResponseEntity<JacksonPage<ProductDTO>> response;
        try {
            response = restTemplate
                .exchange(builder.toUriString(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) { // JacksonPage не может быть создан из пустой Page
            return new JacksonPage<>(Collections.emptyList());
        }

        return response.getBody();
    }

    public ProductDTO findById(Long id) {
        ResponseEntity<ProductDTO> response = restTemplate
                .getForEntity(SUPPLIER_SERVICE_URL + "/" + id, ProductDTO.class);
        return response.getBody();
    }

    public List<ProductDTO> findByName(String name) {
        ResponseEntity<List<ProductDTO>> response = restTemplate
                .exchange(SUPPLIER_SERVICE_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});
        if (response.getBody() == null) {
            return List.of();
        }
        return response.getBody().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public ProductDTO save(ProductDTO product) {
        ResponseEntity<ProductDTO> response;
        try {
            response = restTemplate.postForEntity(SUPPLIER_SERVICE_URL, product, ProductDTO.class);
        } catch (HttpClientErrorException e) {
            throw new InvalidRequestException("Invalid product data");
        }
        return response.getBody();
    }

    public void update(ProductDTO product, Long id) {
        product.setId(id);
        try {
            restTemplate.put(SUPPLIER_SERVICE_URL + "/" + id, product);
        } catch (HttpClientErrorException e) {
            throw new InvalidRequestException("Product with id " + id + " does not exist");
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(SUPPLIER_SERVICE_URL + "/" + id);
        } catch (HttpClientErrorException e) {
            throw new InvalidRequestException("Product with id " + id + " does not exist");
        }
    }
}
