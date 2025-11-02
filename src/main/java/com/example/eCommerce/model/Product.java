package com.example.eCommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoryId")
    private Category category;
}
