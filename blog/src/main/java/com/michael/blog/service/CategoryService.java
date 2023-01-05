package com.michael.blog.service;

import com.michael.blog.entity.Category;
import com.michael.blog.payload.request.CategoryRequest;
import com.michael.blog.payload.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
   CategoryResponse createCategory(CategoryRequest categoryRequest);

   CategoryResponse getCategory(Long categoryId);

   List<CategoryResponse> getAllCategories();

   CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);

   String deleteCategory(Long categoryId);
}
