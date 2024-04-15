package com.example.rqchallenge.feign.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeDto {
    @JsonAlias("id")
    private Long id;

    @JsonProperty("name")
    @JsonAlias("employee_name")
    private String employeeName;

    @JsonProperty("salary")
    @JsonAlias("employee_salary")
    private double employeeSalary;

    @JsonProperty("age")
    @JsonAlias("employee_age")
    private int employeeAge;

    @JsonAlias("profile_image")
    private String profileImage;
}
