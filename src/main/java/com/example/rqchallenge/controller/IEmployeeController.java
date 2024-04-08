package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


public interface IEmployeeController {

    ResponseEntity<List<Employee>> getAllEmployees();

    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString);

    ResponseEntity<Employee> getEmployeeById(@PathVariable String id);


}
