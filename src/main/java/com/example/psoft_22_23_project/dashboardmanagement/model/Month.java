package com.example.psoft_22_23_project.dashboardmanagement.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
@Embeddable
@Getter
@Setter
@Data
@RequiredArgsConstructor
public class Month {
    @NotNull
    private String month;
}
