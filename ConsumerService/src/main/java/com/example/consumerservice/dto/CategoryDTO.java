package com.example.consumerservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class CategoryDTO {
    private long id;

    private String name;

    @JsonIgnore
    private List<ProductDTO> products;
}
