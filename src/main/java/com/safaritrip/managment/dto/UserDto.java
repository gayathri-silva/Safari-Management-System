package com.safaritrip.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    // --- NEW PASSWORD VALIDATION ---
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Must contain one uppercase, one lowercase, one number, and one special character")
    private String password;

    @NotEmpty(message = "Please confirm your password")
    private String confirmPassword;

    @NotEmpty(message = "Email cannot be empty")
    @Email
    private String email;

    // --- NEW PHONE NUMBER VALIDATION ---
    @NotEmpty(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;
}