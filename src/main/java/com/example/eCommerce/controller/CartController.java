package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.cart.CartDTO;
import com.example.eCommerce.DTO.cart.CartItemDTO;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.repositories.CartRepository;
import com.example.eCommerce.service.cart.CartService;
import com.example.eCommerce.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart APIs", description = "APIs for managing cart")
@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;
    private final CartRepository cartRepository;

    public CartController(
            CartService cartService,
            AuthUtil authUtil,
            CartRepository cartRepository
    ) {
        this.cartService = cartService;
        this.authUtil = authUtil;
        this.cartRepository = cartRepository;
    }

    @Operation(summary = "Create the cartItem", description = "API to add the cart into cartItem if null create and add")
    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems) {
       String response = cartService.createOrUpdateCartWithItems(cartItems);
       return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Add the Product to Cart", description = "API to add the product to the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product added to Cart successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid Url"),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @Parameter(description = "Product ID", example = "101")
            @PathVariable Long productId,
            @Parameter(description = "Quantity to add", example = "2")
            @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new  ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }



    @Operation(summary = "Getting All Cart", description = "API to get All cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get All Cart"),
            @ApiResponse(responseCode = "404", description = "Invalid Url"),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Get All UserCart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get Data Successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid Url"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartsByUser(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
      CartDTO cartDTO =   cartService.getCart(emailId,cartId);
      return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @Operation(summary = "Update product quantity in cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated Succesfully"),
            @ApiResponse(responseCode = "404", description = "Invalid Url"),
            @ApiResponse(responseCode = "500", description = "Internal server Error", content = @Content)
    })
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(  @Parameter(description = "Product ID", example = "101")
                                                           @PathVariable Long productId,
                                                       @Parameter(description = "Operation type", example = "add")
                                                       @PathVariable String operation){
       CartDTO cartDTO =  cartService.updateProductQuantityInCart(
                productId, operation.equalsIgnoreCase("delete") ? -1:1);
       return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @Operation(summary = "Delete the product from the cart")
    @Transactional
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                         @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
