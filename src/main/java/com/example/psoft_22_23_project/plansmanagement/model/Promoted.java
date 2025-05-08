package com.example.psoft_22_23_project.plansmanagement.model;



import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
@Data
public class Promoted {
    @Column(name = "Is_Promoted")
    @NotNull
    private Boolean promoted;

}
