package com.example.rqchallenge.feign.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeApiResponse {
    private String status;
    private String message;
    private EmployeeDto data;

    public boolean isSuccessResponse() {
        return "success".equalsIgnoreCase(this.status);
    }
}
