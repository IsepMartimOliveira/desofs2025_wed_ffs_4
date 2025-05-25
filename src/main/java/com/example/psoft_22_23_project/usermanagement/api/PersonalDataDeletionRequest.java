package com.example.psoft_22_23_project.usermanagement.api;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class PersonalDataDeletionRequest {

    @NotBlank(message = "Confirmation password cannot be blank")
    private String confirmationPassword;
}
