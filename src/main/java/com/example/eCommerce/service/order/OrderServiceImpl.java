package com.example.eCommerce.service.order;

import com.example.eCommerce.DTO.order.OrderDTO;
import com.example.eCommerce.DTO.order.OrderItemDTO;
import com.example.eCommerce.exception.ApiException;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.*;
import com.example.eCommerce.repositories.*;
import com.example.eCommerce.service.cart.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        // Getting User Cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart" , "email", emailId);
        }

        Address address = addressRepository.findById(addressId).orElseThrow(
                ()-> new ResourceNotFoundException("Address", " addressId", addressId));

        // Create a new order with
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);
        Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        //Get item from the cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new ApiException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        //Update Product Stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);

            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });


        //Send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item-> orderDTO.getOrderItems().add(
                modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);

        return orderDTO;

    }
}
