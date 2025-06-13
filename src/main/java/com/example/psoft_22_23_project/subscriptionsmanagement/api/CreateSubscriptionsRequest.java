package com.example.psoft_22_23_project.subscriptionsmanagement.api;


import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubscriptionsRequest {


        private String name;
        @NotNull
        @Pattern(regexp = "(annually|monthly)")
        private String paymentType;
}
