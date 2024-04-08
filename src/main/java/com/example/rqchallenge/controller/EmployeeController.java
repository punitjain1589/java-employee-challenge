package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


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

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable @NotBlank(message = "Search string cannot be blank") String searchString) {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.getEmployeesByNameSearch(searchString)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable @NotBlank(message = "id cannot be blank") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                employeeService.getEmployeesById(id)
        );
    }

    // Rest of the CRUD operations will be implemented in same way.
}
