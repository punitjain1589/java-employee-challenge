package com.example.rqchallenge.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    private Long id;
    @JsonAlias("name")
    private String employeeName;
    @JsonAlias("salary")
    private Double employeeSalary;
    @JsonAlias("age")
    private Integer employeeAge;
    private String profileImage;
}
