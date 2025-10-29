package com.example.eCommerce.service;

import java.util.List;
import com.example.eCommerce.model.Category;
public interface CategoryService {
    List<Category> getCategoryList();
    void addCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Category category,Long categoryId);
}
