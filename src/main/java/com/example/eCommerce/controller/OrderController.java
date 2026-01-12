package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.order.OrderDTO;
import com.example.eCommerce.DTO.order.OrderRequestDTO;
import com.example.eCommerce.DTO.payment.StripePaymentDTO;
import com.example.eCommerce.service.order.OrderService;
import com.example.eCommerce.service.payment.StripeService;
import com.example.eCommerce.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.service.PaymentIntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {


    private OrderService orderService;
    private AuthUtil authUtil;
    private StripeService stripeService;

    public OrderController(StripeService stripeService, OrderService orderService, AuthUtil authUtil) {
        this.stripeService = stripeService;
        this.orderService = orderService;
        this.authUtil = authUtil;
    }


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProduct(@PathVariable String paymentMethod,
                                                 @RequestBody OrderRequestDTO orderRequestDTO) {
       String emailId = authUtil.loggedInEmail();
       OrderDTO orderDTO =  orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
       return new  ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stripePaymentDTO) throws StripeException {
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(),HttpStatus.CREATED);
    }
}
