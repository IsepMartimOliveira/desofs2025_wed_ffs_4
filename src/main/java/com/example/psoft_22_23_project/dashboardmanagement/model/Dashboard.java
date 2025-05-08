package com.example.psoft_22_23_project.dashboardmanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TotalActiveSubscriptions totalActiveSubscriptions;
    @Embedded
    private TotalCanceledSubscriptions totalCanceledSubscriptions;
    @Embedded
    private TotalRevenue totalRevenue;
    @Embedded
    private PlanName planName;
    @Embedded
    private Month month;


    public Dashboard( Integer totalActiveSubscriptions,Integer totalCanceledSubscriptions, Double totalRevenue,String planName,String month) {
        this.planName = new PlanName();
        this.month=new Month();
        this.totalActiveSubscriptions = new TotalActiveSubscriptions();
        this.totalCanceledSubscriptions = new TotalCanceledSubscriptions();
        this.totalRevenue = new TotalRevenue();
        this.month.setMonth(month);
        this.planName.setPlanName(planName);
        this.totalActiveSubscriptions.setTotalActiveSubscriptions(totalActiveSubscriptions);
        this.totalCanceledSubscriptions.setTotalCanceledSubscriptions(totalCanceledSubscriptions);
        this.totalRevenue.setTotalRevenue(totalRevenue);

    }


}