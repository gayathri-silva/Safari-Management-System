package com.safaritrip.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuideCreationDto {

    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Contact number is required")
    private String contactNumber;
    @NotEmpty(message = "License number is required")
    private String licenseNumber;

    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "Email is required")
    @Email
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
}