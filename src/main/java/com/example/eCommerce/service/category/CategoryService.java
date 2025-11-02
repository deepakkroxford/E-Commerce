package com.example.eCommerce.service.category;

import com.example.eCommerce.DTO.category.CategoryDTO;
import com.example.eCommerce.DTO.category.CategoryResponse;

public interface CategoryService {
    CategoryResponse getCategoryList(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO category,Long categoryId);
}
