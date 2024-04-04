package com.example.consumerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {
    private Long id;

    @NotNull(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private String description;

    @NotNull (message = "Price is required")
    @Positive (message = "Price must be greater than 0")
    private BigDecimal price;

    private CategoryDTO category;
}
