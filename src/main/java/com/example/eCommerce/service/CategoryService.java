package com.example.eCommerce.service;

import java.util.List;

import com.example.eCommerce.DTO.CategoryDTO;
import com.example.eCommerce.DTO.CategoryResponse;
import com.example.eCommerce.model.Category;
public interface CategoryService {
    CategoryResponse getCategoryList(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO category,Long categoryId);
}
