package com.example.supplierservice.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties("pageable")
public class JacksonPage<T> extends PageImpl<T> { // потому что Page не сериализуется Jackson'ом
    public JacksonPage(List<T> content, int number, int size, long totalElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public JacksonPage(List<T> content) {
        super(content);
    }
}
