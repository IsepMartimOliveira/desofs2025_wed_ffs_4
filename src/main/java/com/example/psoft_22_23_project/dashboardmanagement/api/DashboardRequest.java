package com.example.psoft_22_23_project.dashboardmanagement.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.Date;
@Data
public class DashboardRequest {

    @NotNull
    @JsonFormat( pattern = "yyyy-MM-dd")
    private Date startDate;

    @NotNull
    @JsonFormat( pattern = "yyyy-MM-dd")
    private Date endDate;


}