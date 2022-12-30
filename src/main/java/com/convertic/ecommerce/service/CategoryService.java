package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Category;
import com.convertic.ecommerce.repository.CategoryRepository;
import com.convertic.ecommerce.repository.ProductRepository;
import com.convertic.ecommerce.web.dto.CategoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;


    public static CategoryDto mapToDto(Category category, Long productsCount){
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productsCount
        );
    }

    public List<CategoryDto> findAll(){
        log.debug("Request to get all Categories");
        return this.categoryRepository.findAll()
                .stream()
                .map(category -> mapToDto(category,productRepository.countAllByCategoryId(category.getId())))
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id)
                .map(category -> mapToDto(category,productRepository.countAllByCategoryId(category.getId())))
                .orElse(null);
    }

    public CategoryDto create(CategoryDto categoryDto) {
        log.debug("Request to create Category : {}", categoryDto);
        return mapToDto(categoryRepository.save(
                new Category(categoryDto.getName(),categoryDto.getDescription())), 0L);
    }


}