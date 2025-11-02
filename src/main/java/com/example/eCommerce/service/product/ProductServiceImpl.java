package com.example.eCommerce.service.product;

import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.DTO.product.ProductResponse;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.Category;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.repositories.CategoryRepository;
import com.example.eCommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","id",categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImageUrl("default.png");
        double specialPrice = product.getPrice()-((product.getDiscount()*0.01)* product.getPrice());
        product.setSpecialPrice(specialPrice);
        productRepository.save(product);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map
                (product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","id",categoryId));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream().map
                        (product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
        List<ProductDTO> productDTOS = products.stream().map
                        (product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        //get the existing product
        //update the product info with user shared
        Product productFromDb = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product" ,"id", productId));

        Product product = modelMapper.map(productDTO, Product.class);
        //update the product info with user shared
        productFromDb.setProductName(product.getProductName());
        productFromDb.setProductDescription(product.getProductDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct =  productRepository.save(productFromDb);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }


}
