package com.example.eCommerce.service.order;

import com.example.eCommerce.DTO.order.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
