package com.example.eCommerce.service.product;

import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.DTO.product.ProductResponse;
import com.example.eCommerce.model.Product;
import org.springframework.web.bind.annotation.PathVariable;


public interface ProductService {
    ProductDTO addProduct(ProductDTO product, Long categoryId);
    ProductResponse getAllProducts();
    ProductResponse searchByCategory(Long categoryId);
    ProductResponse searchProductByKeyword(String keyword);
    ProductDTO updateProduct(ProductDTO product, Long productId);
    ProductDTO deleteProduct(Long productId);
}
