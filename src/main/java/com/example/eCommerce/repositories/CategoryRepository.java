package com.example.eCommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.eCommerce.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}
