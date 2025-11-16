package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.DTO.product.ProductResponse;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Add Product", description = "API to Add Product through the categoryId")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
         ProductDTO savedProductDTO =    productService.addProduct(productDTO,categoryId);
         return new  ResponseEntity<>(savedProductDTO,HttpStatus.CREATED);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Get AllProduct", description = "API to Get All Product")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                          @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                          @RequestParam(name="sortBy", required = false, defaultValue = "productId")String sortBy,
                                                          @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Get Product By Category", description = "API to Get Product By Category")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId, @RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                                @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                                @RequestParam(name="sortBy", required = false, defaultValue = "productId")String sortBy,
                                                                @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber, pageSize, sortBy, sortOrder);
        return new  ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Get Product By Keywords", description = "API to Get product By Keyword")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,@RequestParam(name="pageNumber" , required = false, defaultValue = "0")Integer pageNumber,
                                                                @RequestParam(name="pageSize", required = false, defaultValue = "5")Integer pageSize,
                                                                @RequestParam(name="sortBy", required = false, defaultValue = "productName")String sortBy,
                                                                @RequestParam(name="sortOrder", required = false, defaultValue = "asc")String sortOrder) {
        ProductResponse productResponse=    productService.searchProductByKeyword(keyword,pageNumber, pageSize, sortBy, sortOrder);
        return new  ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Update Product By Id", description = "API to Update the Product")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId) {
        ProductDTO updatedProductDto=productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updatedProductDto,HttpStatus.OK);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Delete Product", description = "API to Delete product By ProductId")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProduct  = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @Tag(name = "Product APIs", description = "APIs for managing Product")
    @Operation(summary = "Add Image to Product", description = "API to Add image To Product By ProductId")
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedImage = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedImage,HttpStatus.OK);
    }

}
