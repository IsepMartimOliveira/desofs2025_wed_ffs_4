package com.example.psoft_22_23_project.usermanagement.api;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;

import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateUserRequest {
    @NotNull
    @NotBlank
    @Email
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 12)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Password must be at least 12 characters and include at least one uppercase letter, one digit, and one special character")
    private String password;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    private Integer phoneNumber;

    @NotNull
    @Min(13)
    @Max(100)
    private Integer age;


    private String location;

    @Pattern(regexp = "^[a-zA-Z0-9\\s,.'-]{0,100}$", message = "City contains invalid characters")
    @Size(max = 100, message = "City name is too long")
    private String city;
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.'-]{0,100}$", message = "Country contains invalid characters")
    @Size(max = 100, message = "Country name is too long")
    private String country;
    private String postalCode;
}