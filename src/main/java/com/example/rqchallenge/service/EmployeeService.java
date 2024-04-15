package com.example.rqchallenge.service;

import com.example.rqchallenge.annotation.EnableExceptionHandling;
import com.example.rqchallenge.annotation.EnableExecutionTimeLogging;
import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.feign.EmployeeApiClient;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.mapper.IEmployeeMapper;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@Service
@Validated
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
    public Employee getEmployeeById(String id) {
        EmployeeApiResponse apiResponse = employeeApiClient.getEmployeeById(id);
        validateApiResponse(apiResponse);
        if (null == apiResponse.getData()) throw new DataNotFoundException("No Data found on the API for given Id");
        return employeeMapper.maptoModel(apiResponse.getData());
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().stream()
                .filter(employee -> null != employee.getEmployeeSalary())
                .map(Employee::getEmployeeSalary)
                .max(Comparator.naturalOrder())
                .map(Double::intValue)
                .orElse(0);
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparingDouble(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .toList();
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public Employee createEmployee(@Valid CreateEmployeeRequest createEmployeeRequest) {
        EmployeeApiResponse apiResponse = employeeApiClient.createEmployee(createEmployeeRequest);
        validateApiResponse(apiResponse);
        return employeeMapper.maptoModel(apiResponse.getData());
    }

    @Override
    @EnableExecutionTimeLogging
    @EnableExceptionHandling
    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);
        validateApiResponse(employeeApiClient.deleteEmployeeById(id));
        return employee.getEmployeeName();
    }

}
