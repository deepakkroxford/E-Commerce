package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.cart.CartDTO;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.repositories.CartRepository;
import com.example.eCommerce.service.cart.CartService;
import com.example.eCommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new  ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartsByUser(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
      CartDTO cartDTO =   cartService.getCart(emailId,cartId);
      return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,@PathVariable String operation){
       CartDTO cartDTO =  cartService.updateProductQuantityInCart(
                productId, operation.equalsIgnoreCase("delete") ? -1:1);
       return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                         @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
