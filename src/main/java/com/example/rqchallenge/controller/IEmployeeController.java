package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;


public interface IEmployeeController {

    ResponseEntity<List<Employee>> getAllEmployees();

    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable @NotBlank(message = "Search string cannot be blank") String searchString);

    ResponseEntity<Employee> getEmployeeById(@PathVariable @NotBlank(message = "id cannot be blank") String id);

    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    ResponseEntity<Employee> createEmployee(@RequestBody @Valid Map<String, Object> employeeInput);

    ResponseEntity<String> deleteEmployeeById(@PathVariable @NotBlank(message = "id cannot be blank") String id);


}
