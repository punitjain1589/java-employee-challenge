package com.example.rqchallenge.mapper;

import com.example.rqchallenge.feign.dto.EmployeeDto;
import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IEmployeeMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    public EmployeeDto maptoDto(Employee employee);

    public Employee maptoModel(EmployeeDto employeeDto);

    default <T> List<T> mapToModelList(List<?> entityList, Class<T> type) {
        return entityList.stream().map(entity -> objectMapper.convertValue(entity,type)).toList();
    }

    default <E,T> T mapToType(E entity, Class<T> type) {
        return objectMapper.convertValue(entity,type);
    }
}
