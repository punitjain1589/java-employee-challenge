package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.ApiResponseException;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(@Valid CreateEmployeeRequest createEmployeeRequest);

    String deleteEmployeeById(String id);

    @SneakyThrows(ApiResponseException.class)
    default void validateApiResponse(EmployeeListApiResponse apiResponse) {
        if (!apiResponse.isSuccessResponse()) {
            throw new ApiResponseException("Received Failure response from dummy API");
        }
    }

    @SneakyThrows(ApiResponseException.class)
    default void validateApiResponse(EmployeeApiResponse apiResponse) {
        if (!apiResponse.isSuccessResponse()) {
            throw new ApiResponseException("Received Failure response from dummy API");
        }
    }
}
