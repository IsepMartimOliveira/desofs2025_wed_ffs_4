package com.example.psoft_22_23_project.plansmanagement.model;

import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
@Data
public class AnnualFee {
    @Column(name = "Annual_Fee")
    @NotNull
    @Min(0)
    private double annualFee;

    public void setAnnualFee(double annualFee) {
        if (annualFee < 0) {
            throw new IllegalArgumentException("Annual_Fee must be positive");
        }
        this.annualFee = annualFee;
    }
}
