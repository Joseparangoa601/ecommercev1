package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Product;
import com.convertic.ecommerce.domain.enums.ProductStatus;
import com.convertic.ecommerce.repository.CategoryRepository;
import com.convertic.ecommerce.repository.ProductRepository;
import com.convertic.ecommerce.web.dto.ProductDto;
import com.convertic.ecommerce.web.dto.ReviewDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
//@ApplicationScoped
@Transactional
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public static ProductDto mapToDto(Product product){
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus().name(),
                product.getSalesCounter(),
                product.getReviews().stream().map(ReviewService::mapToDto).collect(Collectors.toSet()),
                product.getCategory().getId()
        );
    }

    public List<ProductDto> findAll() {
        log.debug("Request to get all Products");
        return productRepository.findAll()
                .stream()
                .map(ProductService::mapToDto)
                .collect(Collectors.toList());
    }

    public ProductDto findById(Long id) {
        log.debug("Request to get Product : {}", id);
        return this.productRepository.findById(id)
                .map(ProductService::mapToDto)
                .orElse(null);
    }
    @Autowired
    CategoryRepository categoryRepository;
    public ProductDto create(ProductDto productDto) {
        log.debug("Request to create Product : {}", productDto);
        return mapToDto(
                this.productRepository.save(
                        new Product(
                                productDto.getName(),
                                productDto.getDescription(),
                                productDto.getPrice(),
                                ProductStatus.valueOf(productDto.getStatus()),
                                productDto.getSalesCounter(),
                                Collections.emptySet(),
                                categoryRepository.findById(productDto.getCategoryId()).orElse(null)
                        )
                )
        );
    }
}
