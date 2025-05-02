package com.example.psoft_22_23_project.subscriptionsmanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlansDetailsView {
    @Schema(description = "The name of the plan")
    private String name;

    @Schema(description = "The description of the plan")
    private String description;
    @Schema(description = "The number of minutes of the plan")
    private String numberOfMinutes;
    @Schema(description = "The maximum number of users of the plan")
    private String maximumNumberOfUsers;
    @Schema(description = "The number of music collection of the plan")
    private String musicCollection;

    @Schema(description = "The music suggestion of the plan")
    private String musicSuggestion;
    @Schema(description = "The monthly fee of the plan")
    private String monthlyFee;
    @Schema(description = "The annual fee of the plan")
    private String annualFee;

    @Schema(description = "If Plan is active or not")
    private String active;
    @Schema(description = "If Plan is promoted or not")
    private String promoted;

    public String getMonthlyFee() {
        String money = " €";
        return monthlyFee+money;
    }

    public String getAnnualFee() {
        String money = " €";
        return annualFee+money;
    }

}
