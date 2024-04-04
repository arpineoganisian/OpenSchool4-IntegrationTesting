package com.example.supplierservice.repository;

import com.example.supplierservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query(value = "TRUNCATE TABLE product RESTART IDENTITY", nativeQuery = true)
    @Modifying
    void deleteAllAndResetId();
}
