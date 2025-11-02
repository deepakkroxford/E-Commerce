package com.example.eCommerce.controller;


import com.example.eCommerce.DTO.category.CategoryDTO;
import com.example.eCommerce.DTO.category.CategoryResponse;
import com.example.eCommerce.service.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getCategoryList(@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                            @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                            @RequestParam(name="sortBy", required = false, defaultValue = "categoryId")String sortBy,
                                                            @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {

        CategoryResponse categoryResponse =  categoryService.getCategoryList(pageNumber, pageSize, sortBy, sortOrder);
       return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }


    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO=categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }


}