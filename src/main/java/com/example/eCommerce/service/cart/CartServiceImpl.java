package com.example.eCommerce.service.cart;


import com.example.eCommerce.DTO.cart.CartDTO;
import com.example.eCommerce.DTO.cart.CartItemDTO;
import com.example.eCommerce.DTO.product.ProductDTO;
import com.example.eCommerce.exception.ApiException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.Cart;
import com.example.eCommerce.model.CartItem;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.repositories.CartItemRepository;
import com.example.eCommerce.repositories.CartRepository;
import com.example.eCommerce.repositories.ProductRepository;
import com.example.eCommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //find existing cart or create one
        Cart cart = createCart();

        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new ResourceNotFoundException("Product", "ProductId", productId)
        );

        // Retrieve Product Details
       CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),productId
        );

        //Perform validation
       if(cartItem!=null){
           throw new ApiException("Product " + product.getProductName() + " already exists in cart");
       }

       if(product.getQuantity()==0){
           throw new ApiException("Product " + product.getProductName() + " has no quantity");
       }

       if(product.getQuantity()<quantity){
           throw new ApiException("Please, make an order of the  " + product.getProductName() + " less than or equal to the quantity "+  product.getQuantity() + ".");
       }
        // Create Cart item
        CartItem newCartItem = new CartItem();
       newCartItem.setProduct(product);
       newCartItem.setCart(cart);
       newCartItem.setQuantity(quantity);
       newCartItem.setDiscount(product.getDiscount());
       newCartItem.setProductPrice(product.getSpecialPrice());

        // save cart item
        cartItemRepository.save(newCartItem);

        cart.getCartItems().add(newCartItem);
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice()+ (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // return updated cart
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream  = cartItems.stream().
                map(item->{
                    ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
       List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new ApiException("No carts found");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> product = cart.getCartItems().stream().map(
                    cartItem -> {
                        ProductDTO productDTO = modelMapper.map(cartItem.getProduct(),ProductDTO.class);
                        productDTO.setQuantity(cartItem.getQuantity());
                        return productDTO;
                    }).toList();
            cartDTO.setProducts(product);
            return cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
       Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
       if(cart==null){
           throw new ResourceNotFoundException("Cart", "CartId", cartId);
       }
       CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
       cart.getCartItems().forEach(cartItem->cartItem.getProduct().setQuantity(cartItem.getQuantity()));
       List<ProductDTO> products = cart.getCartItems().stream().
               map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).
               toList();

       cartDTO.setProducts(products);
       return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                ()-> new ResourceNotFoundException("Cart", "CartId", cartId)
        );
        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new ResourceNotFoundException("Product", "ProductId", productId)
        );

        if(product.getQuantity()==0){
            throw new ApiException("Product " + product.getProductName() +
                    " has no quantity");
        }

        if(product.getQuantity()<quantity){
            throw new ApiException("Please, make an order of the  " +
                    product.getProductName() + " less than or equal to the quantity "+
                    product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem==null){
            throw new ApiException("Product " +
                    product.getProductName() +
                    " not available in the cart");
        }

        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity<0) {
            throw new ApiException("The resulting quantity cannot be negative");
        }

        if(newQuantity ==0 ) {
            deleteProductFromCart(cartId, productId);
        } else {

            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(
                item ->{
                    ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                }
        );
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                ()-> new ResourceNotFoundException("Cart", "CartId", cartId)
        );
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product", "Product", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product" + cartItem.getProduct().getProductName() + " removed from the cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                ()-> new ResourceNotFoundException("Cart", "CartId", cartId)
        );
        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new ResourceNotFoundException("Product", "ProductId", productId)
        );

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem==null){
            throw new ApiException("Product " + product.getProductName() +
                    " not available in the cart");
        }

        double cartPrice = cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItem = cartItemRepository.save(cartItem);


    }

    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // get user email
        String email = authUtil.loggedInEmail();

        // check if an existing cart is available or create a new one
        Cart existingCart  = cartRepository.findCartByEmail(email);
        if(existingCart==null){
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }
        double totalPrice = 0.00;
        // process each item in the request to add to the cart
        for(CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // find the product by Id
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new ResourceNotFoundException("Product", "ProductId", productId)
            );
            // Directly update product stock and total price
           // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;
            // create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }
        //update the carts and total price
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "cart created/updated with the new items successfully";
    }


    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return  userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart =  cartRepository.save(cart);
        return newCart;
    }
}
