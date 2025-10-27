package com.example.eCommerce.service;

import com.example.eCommerce.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private List<Category> categoryList = new ArrayList<>();

    @Override
    public List<Category> getCategoryList() {
        return categoryList;
    }

    @Override
    public void addCategory(Category category) {
        categoryList.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryList.stream().
                filter(c->c.getCategoryId().
                        equals(categoryId)).findFirst().
                                orElse(null);
        categoryList.remove(category);
        return "Category Deleted with id: " + categoryId;
    }
}
