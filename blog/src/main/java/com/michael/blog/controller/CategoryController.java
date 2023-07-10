package com.michael.blog.controller;

import com.michael.blog.payload.request.CategoryRequest;
import com.michael.blog.payload.response.CategoryResponse;
import com.michael.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "CRUD REST APIs for Category Resource")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create Category Rest API",
            description = "Create Category REST API is used to save category into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping("/category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), CREATED);
    }

    @Operation(summary = "Get Category By Id Rest API",
            description = "Get Category By Id REST API is used to fetch single category from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.getCategory(categoryId), OK);
    }

    @Operation(summary = "Get All Сategories Rest API",
            description = "Get Сategories By Id REST API is used to fetch all categories from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), OK);
    }

    @Operation(summary = "Update Category  Rest API",
            description = "Update Category REST API is used to update category by Id from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping("/category/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                           @Valid @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, categoryRequest), OK);
    }

    @Operation(summary = "Delete Category  Rest API",
            description = "Delete Category REST API is used to delete particular category from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId), OK);
    }
}
