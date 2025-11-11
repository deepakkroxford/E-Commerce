package com.example.eCommerce.DTO.cart;

import com.example.eCommerce.DTO.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO productDTO;
    private Integer  quantity;
    private Double discount;
    private double productPrice;
}
