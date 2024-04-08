package com.example.rqchallenge.mapper;

import com.example.rqchallenge.feign.dto.EmployeeDto;
import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeMapper implements IEmployeeMapper {

    public EmployeeDto maptoDto(Employee employee) {
        return  mapToType(employee,EmployeeDto.class);
    }


    public Employee maptoModel(EmployeeDto employeeDto) {
        return  mapToType(employeeDto, Employee.class);
    }
}
