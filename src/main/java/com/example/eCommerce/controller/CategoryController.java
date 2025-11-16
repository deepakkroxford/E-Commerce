package com.example.eCommerce.controller;


import com.example.eCommerce.DTO.category.CategoryDTO;
import com.example.eCommerce.DTO.category.CategoryResponse;
import com.example.eCommerce.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Tag(name = "Categories APIs", description = "APIs for managing categories")
    @Operation(summary = "Get all Category", description = "API to Get  All Category that exist in the database")
    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getCategoryList(@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                            @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                            @RequestParam(name="sortBy", required = false, defaultValue = "categoryId")String sortBy,
                                                            @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {

        CategoryResponse categoryResponse =  categoryService.getCategoryList(pageNumber, pageSize, sortBy, sortOrder);
       return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @Tag(name = "Categories APIs", description = "APIs for managing categories")
    @Operation(summary = "Create Category", description = "API to create Category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category is created"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO=categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @Tag(name = "Categories APIs", description = "APIs for managing categories")
    @Operation(summary = "Delete Category", description = "API to Delete Category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category is Deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Parameter(description = "Give the Category Id that you want to delete") @PathVariable Long categoryId) {
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @Tag(name = "Categories APIs", description = "APIs for managing categories")
    @Operation(summary = "Update Category", description = "API to Update Category")
    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }


}