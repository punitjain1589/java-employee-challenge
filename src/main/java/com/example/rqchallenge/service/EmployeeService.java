package com.example.rqchallenge.service;

import com.example.rqchallenge.annotation.EnableExceptionHandling;
import com.example.rqchallenge.annotation.EnableExecutionTimeLogging;
import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.feign.EmployeeApiClient;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.mapper.IEmployeeMapper;
import com.example.rqchallenge.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService implements IEmployeeService{

    @Autowired
    private EmployeeApiClient employeeApiClient;

    @Autowired
    private IEmployeeMapper employeeMapper;

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public List<Employee> getAllEmployees() {
        EmployeeListApiResponse apiResponse = employeeApiClient.getAllEmployees();
        validateApiResponse(apiResponse);
        return employeeMapper.mapToModelList(apiResponse.getData(), Employee.class);
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream()
                .filter(employee -> null != employee.getEmployeeName() ? employee.getEmployeeName().contains(searchString) : false)
                .toList();
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public Employee getEmployeesById(String id) {
        EmployeeApiResponse apiResponse = employeeApiClient.getEmployeeById(id);
        validateApiResponse(apiResponse);
        if (null == apiResponse.getData()) throw new DataNotFoundException("No Data found on the API for given Id");
        return employeeMapper.maptoModel(apiResponse.getData());
    }

}
