package com.example.psoft_22_23_project.subscriptionsmanagement.model;

import lombok.Getter;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Getter
@Embeddable
public class StartDate {

    private String startDate;

    public StartDate() {

        this.startDate = String.valueOf(LocalDate.now());
    }
}
