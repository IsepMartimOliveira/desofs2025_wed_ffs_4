package com.example.psoft_22_23_project.subscriptionsmanagement.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;


@Getter
@Setter
@Embeddable
public class EndDate {

    //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private String endDate;


    public EndDate(String paymenType) {

        LocalDate date = LocalDate.now();

        if (paymenType.equals("annually")) {
            this.endDate = String.valueOf(date.plusYears(1));
        }else{
            this.endDate = "Not yet defined";
        }
    }

    public EndDate() {

    }

}
