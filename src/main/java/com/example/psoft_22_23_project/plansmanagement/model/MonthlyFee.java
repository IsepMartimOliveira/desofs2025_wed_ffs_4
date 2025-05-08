package com.example.psoft_22_23_project.plansmanagement.model;


import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
@Data
public class MonthlyFee {
    @Column(name = "Monthly_Fee")
    @NotNull
    @Min(0)
    private double monthlyFee;

    public void setMonthlyFee(double monthlyFee) {
        if (monthlyFee < 0) {
            throw new IllegalArgumentException("Monthly Fee must be positive");
        }
        this.monthlyFee = monthlyFee;
    }

}
