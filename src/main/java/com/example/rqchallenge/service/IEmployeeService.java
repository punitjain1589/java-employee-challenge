package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.ApiResponseException;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.model.Employee;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeesById(String id);

    @SneakyThrows(ApiResponseException.class)
    default void validateApiResponse(EmployeeListApiResponse apiResponse) {
        if (!apiResponse.isSuccessResponse()) {
            throw new ApiResponseException("Failed to fetch employees from dummy API");
        }
    }

    @SneakyThrows(ApiResponseException.class)
    default void validateApiResponse(EmployeeApiResponse apiResponse) {
        if (!apiResponse.isSuccessResponse()) {
            throw new ApiResponseException("Failed to fetch employees from dummy API");
        }
    }


}
