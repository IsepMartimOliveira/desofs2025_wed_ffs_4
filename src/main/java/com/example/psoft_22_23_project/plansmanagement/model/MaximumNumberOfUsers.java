package com.example.psoft_22_23_project.plansmanagement.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
@Data
public class MaximumNumberOfUsers {
    @Column(name = "Maximum_Number_Of_Users")
    @NotNull
    @Min(0)
    private Integer maximumNumberOfUsers;

    public void setMaximumNumberOfUsers(Integer maximumNumberOfUsers) {
        if (maximumNumberOfUsers == null || maximumNumberOfUsers < 0) {
            throw new IllegalArgumentException("Maximum Number Of Users must be positive");
        }
        this.maximumNumberOfUsers = maximumNumberOfUsers;
    }
}
