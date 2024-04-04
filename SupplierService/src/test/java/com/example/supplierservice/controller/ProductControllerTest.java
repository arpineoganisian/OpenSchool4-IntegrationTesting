package com.example.supplierservice.controller;

import com.example.supplierservice.model.Product;
import com.example.supplierservice.service.ProductService;
import com.example.supplierservice.util.JacksonPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Container
    @ServiceConnection // Spring configures it by itself (instead of DynamicPropertySource)
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    ProductService productService;

    @BeforeAll
    static void testConnection() throws IOException, InterruptedException {
        POSTGRESQL_CONTAINER.start();
        assertTrue(POSTGRESQL_CONTAINER.isRunning());
        POSTGRESQL_CONTAINER.execInContainer("psql -U test -d test -c \"DELETE FROM product;\"");
    }

    @AfterAll
    static void closeConnection() {
        POSTGRESQL_CONTAINER.stop();
    }

    @BeforeEach
    void beforeEach() {
        productService.deleteAll();

        Product product = new Product();
        product.setName("Product");
        product.setDescription("Description");
        product.setPrice(BigDecimal.ONE);

        testRestTemplate.postForEntity("/supplier/products", product, Product.class);
    }

    @Test
    void createProductsFailTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> requestBody = new HttpEntity<>(null, headers);

        ResponseEntity<Product> response = testRestTemplate.exchange("/supplier/products",
                HttpMethod.POST,
                requestBody,
                new ParameterizedTypeReference<>() {});

        assertEquals(400, response.getStatusCodeValue());
    }


    @Test
    void getAllProductsSuccessTest() {
        ResponseEntity<JacksonPage<Product>> response = testRestTemplate.exchange("/supplier/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getProductByIdSuccessTest() {
        ResponseEntity<Product> response = testRestTemplate
                .getForEntity("/supplier/products/{id}", Product.class, 1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product", response.getBody().getName());
        assertEquals("Description", response.getBody().getDescription());
    }

    @Test
    void whenProductIdIsWrong_getProductByIdTest() {
        ResponseEntity<Product> response = testRestTemplate
                .getForEntity("/supplier/products/{id}", Product.class, 100);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }

    @Test
    void updateProductSuccessTest() {
        Product product = new Product();
        product.setName("Product2");
        product.setDescription("Description2");
        product.setPrice(BigDecimal.ONE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> requestBody = new HttpEntity<>(product, headers);

        ResponseEntity<Void> response = testRestTemplate.exchange("/supplier/products/{id}",
                HttpMethod.PUT,
                requestBody,
                new ParameterizedTypeReference<>() {},
                1);

        assertEquals(202, response.getStatusCodeValue());
    }

    @Test
    void whenProductIdIsWrong_updateProductFailTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> requestBody = new HttpEntity<>(new Product(), headers);

        ResponseEntity<Void> response = testRestTemplate.exchange("/supplier/products/{id}",
                HttpMethod.PUT,
                requestBody,
                new ParameterizedTypeReference<>() {},
                100);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void whenProductIsNull_updateProductFailTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> requestBody = new HttpEntity<>(null, headers);

        ResponseEntity<Void> response = testRestTemplate.exchange("/supplier/products/{id}",
                HttpMethod.PUT,
                requestBody,
                new ParameterizedTypeReference<>() {},
                1);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void deleteProductSuccessTest() {
        ResponseEntity<Void> response = testRestTemplate.exchange("/supplier/products/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                1);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void deleteProductFailTest() {
        ResponseEntity<Void> response = testRestTemplate.exchange("/supplier/products/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                100);

        assertEquals(400, response.getStatusCodeValue());
    }
}