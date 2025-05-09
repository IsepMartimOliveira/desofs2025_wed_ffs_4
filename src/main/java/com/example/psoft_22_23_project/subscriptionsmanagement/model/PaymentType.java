package com.example.psoft_22_23_project.subscriptionsmanagement.model;

import lombok.Data;
import lombok.Getter;

import jakarta.persistence.Embeddable;


@Getter
@Data
@Embeddable
public class PaymentType {


    private String paymentType;

    public PaymentType(String paymentType ) {
        this.paymentType = paymentType;
    }

    public PaymentType() {

    }
}
