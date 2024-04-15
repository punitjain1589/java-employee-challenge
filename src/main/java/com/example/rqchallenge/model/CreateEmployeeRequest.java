package com.example.rqchallenge.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequest {

    @NotEmpty
    @Size(min = 2,max = 100)
    private String name;

    @NotNull
    @Positive
    private Double salary;

    @NotNull
    @Positive
    private Integer age;
}
