package com.example.psoft_22_23_project.subscriptionsmanagement.model;

import com.example.psoft_22_23_project.plansmanagement.model.Plans;
import lombok.Data;

@Data
public class PlansDetails {
    private Plans plans;


    public PlansDetails(Plans plan) {
        this.plans = plan;

    }
}
