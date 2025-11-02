package com.example.eCommerce.service.category;
import com.example.eCommerce.DTO.category.CategoryDTO;
import com.example.eCommerce.DTO.category.CategoryResponse;
import com.example.eCommerce.exception.ApiException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.Category;
import com.example.eCommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getCategoryList(Integer pageNumber, Integer PageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, PageSize, sortByAndOrder);
        Page<Category> categoriesPage = categoryRepository.findAll(pageDetails);
       List<Category> categoryList= categoriesPage.getContent();
       if(categoryList.isEmpty()){
           throw new ApiException("no category found");
       } else{
           List<CategoryDTO> categoryDTOS = categoryList.stream()
                   .map(category -> modelMapper.map(category, CategoryDTO.class))
                   .toList();
           CategoryResponse categoryResponse = new CategoryResponse();
           categoryResponse.setContent(categoryDTOS);
           categoryResponse.setPageNumber(categoriesPage.getNumber());
           categoryResponse.setPageSize(categoriesPage.getSize());
           categoryResponse.setTotalPages(categoriesPage.getTotalPages());
           categoryResponse.setTotalElement(categoriesPage.getTotalElements());
           categoryResponse.setLastPage(categoriesPage.isLast());
           return categoryResponse;
       }
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        // Check if category already exists
        Category existingCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (existingCategory != null) {
            throw new ApiException("Category already exists with name " + categoryDTO.getCategoryName());
        }
        // Map DTO → Entity
        Category category = modelMapper.map(categoryDTO, Category.class);
        // Save to DB
        Category savedCategory = categoryRepository.save(category);
        // Map back Entity → DTO
        CategoryDTO responseDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        // Add custom message (extra field)
        responseDTO.setMessage("Category added successfully");
        return responseDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
       Category category = categoryRepository.findById(categoryId).orElseThrow(
               () -> new ResourceNotFoundException("Category","categoryId", categoryId)
       );

        CategoryDTO responseCategory = modelMapper.map(category, CategoryDTO.class);
        responseCategory.setMessage(responseCategory.getCategoryName()+ " Category deleted successfully");
        return responseCategory;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(existingCategory.getCategoryId());
        existingCategory =  categoryRepository.save(category);

        CategoryDTO responseDto= modelMapper.map(existingCategory, CategoryDTO.class);
        responseDto.setMessage("Category updated successfully");
        return responseDto;
    }
}
