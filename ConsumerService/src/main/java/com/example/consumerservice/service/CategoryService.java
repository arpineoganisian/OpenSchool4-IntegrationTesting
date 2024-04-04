package com.example.consumerservice.service;

import com.example.consumerservice.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CategoryService {
    private static final String SUPPLIER_SERVICE_URL = "http://supplier:8080/supplier/categories";
//    private static final String SUPPLIER_SERVICE_URL = "http://localhost:8080/supplier/categories";
    private final RestTemplate restTemplate;

    @Autowired
    public CategoryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CategoryDTO> findAll() {
        ResponseEntity<List<CategoryDTO>> response = restTemplate
                .exchange(SUPPLIER_SERVICE_URL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public CategoryDTO findById(Long id) {
        ResponseEntity<CategoryDTO> response = restTemplate
                .getForEntity(SUPPLIER_SERVICE_URL + "/" + id, CategoryDTO.class);
        return response.getBody();
    }
}
