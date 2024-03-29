package com.michael.blog.service.impl;

import com.michael.blog.entity.Category;
import com.michael.blog.exception.payload.CategoryNotFoundException;
import com.michael.blog.payload.request.CategoryRequest;
import com.michael.blog.payload.response.CategoryResponse;
import com.michael.blog.repository.CategoryRepository;
import com.michael.blog.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new RuntimeException(String.format("Category with name %s already exists", categoryRequest.getName()));
        }
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
        category = categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryResponse getCategory(Long categoryId) {
        Category category = getCategoryFromDBById(categoryId);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = getCategoryFromDBById(categoryId);
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category = categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = getCategoryFromDBById(categoryId);
        categoryRepository.delete(category);
        return String.format("Category with id %s was deleted", categoryId);
    }


    private Category getCategoryFromDBById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id %d not found", categoryId)));
    }
}
