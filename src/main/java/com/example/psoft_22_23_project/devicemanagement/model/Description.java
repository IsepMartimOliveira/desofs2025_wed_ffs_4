package com.example.psoft_22_23_project.devicemanagement.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Embeddable
public class Description {

    @Column(name = "description", nullable = false)
    @NotNull
    @Size(min = 0, max = 50)
    private String description;

    public void setDescription(String description) {
        if (description.length() <= 50 && !description.isBlank()){
            this.description = description;
        } else {
            throw new IllegalArgumentException("Invalid character(s) in 'description', enter a valid designation with 50 character(s)");
        }
    }

    public Description() {
    }
}
