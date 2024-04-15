package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@Validated
public class EmployeeController implements IEmployeeController{

    @Autowired
    private IEmployeeService employeeService;

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.getAllEmployees()
        );
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(
            @PathVariable String searchString) {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.getEmployeesByNameSearch(searchString)
        );
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.getEmployeeById(id)
        );
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid Map<String, Object> employeeInput) {
        CreateEmployeeRequest createEmployeeRequest = mapToCreateEmployeeRequest(employeeInput);
        if (null == createEmployeeRequest) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(createEmployeeRequest));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(
            @PathVariable  String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.deleteEmployeeById(id));
    }

    private CreateEmployeeRequest mapToCreateEmployeeRequest(@Valid Map<String, Object> employeeInput) {
        return CreateEmployeeRequest.builder()
                .name((String) employeeInput.get("name"))
                .age((Integer) employeeInput.get("age"))
                .salary((Double) employeeInput.get("salary"))
                .build();
    }


}
