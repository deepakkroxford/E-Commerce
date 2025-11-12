package com.example.eCommerce.DTO.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private long paymentId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}
