package com.example.psoft_22_23_project.plansmanagement.model;

import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Data
public class Deleted {
    @Column(name = "Is_Deleted")
    private boolean deleted = Boolean.FALSE;

}
