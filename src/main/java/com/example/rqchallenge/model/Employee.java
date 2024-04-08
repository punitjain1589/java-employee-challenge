package com.example.rqchallenge.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    private Long id;
    private String employeeName;
    private double employeeSalary;
    private int employeeAge;
    private String profileImage;
}
