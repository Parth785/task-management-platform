package com.tmp.authservice.dto.request;

import com.tmp.authservice.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(example = "password123")
    private String password;

    @NotBlank(message = "Full name is required")
    @Schema(example = "Parth Lakhani")
    private String fullName;

    @Schema(
    	    example = "USER",
    	    description = "ADMIN role can only be assigned by authenticated administrators"
    	)
    private Role role;
}