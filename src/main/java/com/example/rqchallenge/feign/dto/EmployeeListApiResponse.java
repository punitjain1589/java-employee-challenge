package com.example.rqchallenge.feign.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListApiResponse {
    private String status;
    private String message;
    private List<EmployeeDto> data;

    public boolean isSuccessResponse() {
        return "success".equalsIgnoreCase(this.status);
    }
}
