package com.example.eCommerce.jwt;

import com.example.eCommerce.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;
    @NotBlank
    @Size(min = 2, max = 20)
    @Email
    private String email;

    private Set<String> roles;
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
