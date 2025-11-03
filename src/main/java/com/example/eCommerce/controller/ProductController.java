package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.DTO.product.ProductResponse;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")

public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
         ProductDTO savedProductDTO =    productService.addProduct(productDTO,categoryId);
         return new  ResponseEntity<>(savedProductDTO,HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                          @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                          @RequestParam(name="sortBy", required = false, defaultValue = "productId")String sortBy,
                                                          @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId, @RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                                @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                                @RequestParam(name="sortBy", required = false, defaultValue = "productId")String sortBy,
                                                                @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber, pageSize, sortBy, sortOrder);
        return new  ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                                @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                                @RequestParam(name="sortBy", required = false, defaultValue = "productName")String sortBy,
                                                                @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse=    productService.searchProductByKeyword(keyword,pageNumber, pageSize, sortBy, sortOrder);
        return new  ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {
        ProductDTO updatedProductDto=productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updatedProductDto,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProduct  = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedImage = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedImage,HttpStatus.OK);
    }

}
