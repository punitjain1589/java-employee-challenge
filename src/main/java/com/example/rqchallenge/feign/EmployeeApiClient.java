package com.example.rqchallenge.feign;

import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employeeApiClient", url = "${employee.api.base-url}")
public interface EmployeeApiClient {

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 2000))
    @GetMapping("/api/v1/employees")
    EmployeeListApiResponse getAllEmployees();

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 2000))
    @GetMapping("/api/v1/employee/{id}")
    EmployeeApiResponse getEmployeeById(@PathVariable String id);

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 2000))
    @PostMapping("/api/v1/create")
    EmployeeApiResponse createEmployee(@RequestBody CreateEmployeeRequest createEmployeeRequest);

    @Retryable(value = Exception.class, backoff = @Backoff(delay = 2000))
    @GetMapping("/api/v1/delete/{id}")
    EmployeeApiResponse deleteEmployeeById(@PathVariable String id);
}
