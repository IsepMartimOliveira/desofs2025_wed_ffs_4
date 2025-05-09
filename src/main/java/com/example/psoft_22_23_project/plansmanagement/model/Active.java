package com.example.psoft_22_23_project.plansmanagement.model;
import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
@Embeddable
@Data
public class Active {
    @Column(name = "Is_Active")
    private Boolean active;


}
