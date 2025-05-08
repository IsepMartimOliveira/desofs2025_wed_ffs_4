package com.example.psoft_22_23_project.plansmanagement.model;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Embeddable
@Data
public class Name {
        @Column(name = "Name", unique = true)
        @NotNull
        @Size(min = 1)
        private String name;

        public void setName(String name) {
                if (name == null || name.isBlank()) {
                        throw new IllegalArgumentException("'name' is a mandatory attribute of Plan");
                }
                this.name = name;
        }


}
