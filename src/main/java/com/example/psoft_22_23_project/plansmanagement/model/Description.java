package com.example.psoft_22_23_project.plansmanagement.model;
import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Embeddable
@Data
public class Description {
    @Column(name = "Description")
    @NotNull
    @Size(min = 1)
    private String description;

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is a mandatory attribute of Plan");
        }
        this.description = description;
    }
}
