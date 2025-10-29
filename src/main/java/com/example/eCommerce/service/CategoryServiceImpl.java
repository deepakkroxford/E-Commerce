package com.example.eCommerce.service;
import com.example.eCommerce.exception.ApiException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.Category;
import com.example.eCommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategoryList() {
       List<Category> categoryList= categoryRepository.findAll();
       if(categoryList.isEmpty()){
           throw new ApiException("no category found");
       } else{
           return categoryList;
       }

    }

    @Override
    public void addCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new ApiException("Category already exists with name " + category.getCategoryName());
        } else {
            categoryRepository.save(category);
        }
    }

    @Override
    public String deleteCategory(Long categoryId) {
       Category category = categoryRepository.findById(categoryId).orElseThrow(
               () -> new ResourceNotFoundException("Category","categoryId", categoryId)
       );
        categoryRepository.delete(category);
        return "Category Deleted with id: " + categoryId;
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
         Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
         Category savedCategory = savedCategoryOptional.
                 orElseThrow(()-> new ResourceNotFoundException("Category","categoryId", categoryId));
         savedCategory.setCategoryName(category.getCategoryName());
         savedCategory.setCategoryId(categoryId);
         categoryRepository.save(savedCategory);
         return savedCategory;
    }


}
