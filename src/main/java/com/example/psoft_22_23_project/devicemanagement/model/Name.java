package com.example.psoft_22_23_project.devicemanagement.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Embeddable
public class Name {

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public Name() {
    }
}
