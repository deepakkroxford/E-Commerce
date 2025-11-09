package com.example.eCommerce.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String jwtToken;
    private String username;
    private List<String> roles;
}
