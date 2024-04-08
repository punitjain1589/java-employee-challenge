package com.example.rqchallenge.feign;

import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employeeApiClient", url = "${employee.api.base-url}")
public interface EmployeeApiClient {

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 5000))
    @GetMapping("/api/v1/employees")
    EmployeeListApiResponse getAllEmployees();

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 5000))
    @GetMapping("/api/v1/employee/{id}")
    EmployeeApiResponse getEmployeeById(@PathVariable String id);
}
