package com.example.eCommerce.repositories;

import com.example.eCommerce.model.Category;
import com.example.eCommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    List<Product> findByProductNameLikeIgnoreCase(String keyword);
}
