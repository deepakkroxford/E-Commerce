package com.example.eCommerce.service.product;

import com.example.eCommerce.DTO.cart.CartDTO;
import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.DTO.product.ProductResponse;
import com.example.eCommerce.exception.ApiException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.model.Category;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.repositories.CartItemRepository;
import com.example.eCommerce.repositories.CartRepository;
import com.example.eCommerce.repositories.CategoryRepository;
import com.example.eCommerce.repositories.ProductRepository;
import com.example.eCommerce.service.cart.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String baseUrl;


    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","id",categoryId));
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for(int i=0;i<products.size();i++) {
            if(products.get(i).getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImageUrl("default.png");
            double specialPrice = product.getPrice()-((product.getDiscount()*0.01)* product.getPrice());
            product.setSpecialPrice(specialPrice);
            productRepository.save(product);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct,ProductDTO.class);
        } else {
            throw new ApiException("Product already exists");
        }


    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize, sortByAndOrder);

        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> productList = productPage.getContent();

            List<ProductDTO> productDTOS = productList.stream().map
                            (product -> {
                            ProductDTO productDTO =  modelMapper.map(product, ProductDTO.class);
                            productDTO.setImage(constructImageUrl(product.getImageUrl()));
                            return productDTO;
                            })
                    .toList();
            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDTOS);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalElement(productPage.getTotalElements());
            productResponse.setTotalPages(productPage.getTotalPages());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;

    }


    private String constructImageUrl(String  imageName) {
            return baseUrl.endsWith("/") ? baseUrl + imageName : baseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","id",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize, sortByAndOrder);

        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> productList = productPage.getContent();
        if(productList.isEmpty()) {
            throw new ApiException(category.getCategoryName() +" category does not have any product");
        }

        List<ProductDTO> productDTOS = productList.stream().map
                        (product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElement(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);
        List<Product> productList = productPage.getContent();
        if(productList.isEmpty()) {
            throw new ApiException("Product Not Found With this" + keyword);
        }
        List<ProductDTO> productDTOS = productList.stream().map
                        (product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElement(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
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

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream().map(
                cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream().
                            map(p->modelMapper.map(p.getProduct(), ProductDTO.class)).
                            toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();

        cartDTOs.forEach(cart ->
                cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> {
            cartService.deleteProductFromCart(cart.getCartId(), productId);
        });
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // get the product from DB
        Product productFromDB = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product" ,"id", productId));

        // get the file name from the uploaded image
        String fileName = fileService.uploadImage(path,image);
        // updating the new file name to the product
        productFromDB.setImageUrl(fileName);
        // save the updated product
       Product updatedProduct =  productRepository.save(productFromDB);
        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);

}

}
