package com.example.psoft_22_23_project.usermanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotNull
    @NotBlank
    private String currentPassword;

    @NotNull
    @NotBlank
    @Size(min = 12, max = 24)
    private String newPassword;
}