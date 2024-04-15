package com.example.rqchallenge.controller;

import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.service.IEmployeeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(buildEmployeeList());

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    List<Employee> employees = mapper.readValue(result.getResponse().getContentAsString(),new TypeReference<List<Employee>>() {});
                    assertThat(employees)
                            .usingRecursiveFieldByFieldElementComparator()
                            .isEqualTo(buildEmployeeList());
                });
    }

    @Test
    public void testGetAllEmployees_WhenException() throws Exception {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("---Test Exception---"));
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("---Test Exception---");
                });

    }


    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(buildEmployeeList());

        mockMvc.perform(get("/api/v1/employee/search/it"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    List<Employee> employees = mapper.readValue(result.getResponse().getContentAsString(),new TypeReference<List<Employee>>() {});
                    assertThat(employees)
                            .usingRecursiveFieldByFieldElementComparator()
                            .isEqualTo(buildEmployeeList());
                });
    }

    @Test
    public void testGetEmployeesByNameSearch_WhenException() throws Exception {
        when(employeeService.getEmployeesByNameSearch(anyString())).thenThrow(new RuntimeException("---Test Exception while search by Name---"));
        mockMvc.perform(get("/api/v1/employee/search/it"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("---Test Exception while search by Name---");
                });

    }

    @Test
    public void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(anyString())).thenReturn(buildEmployee());

        mockMvc.perform(get("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    Employee employee = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertThat(employee).isEqualToComparingFieldByField(buildEmployee());
                });
    }


    @Test
    public void testGetEmployeeById_WhenException() throws Exception {
        when(employeeService.getEmployeeById(anyString())).thenThrow(new DataNotFoundException("No Data found for the given id = 2"));
        mockMvc.perform(get("/api/v1/employee/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("No Data found for the given id = 2");
                });

    }


    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(25000);

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    Integer highestSalary = mapper.readValue(result.getResponse().getContentAsString(), Integer.class);
                    assertThat(highestSalary)
                            .isEqualTo(25000);
                });
    }

    @Test
    public void testGetHighestSalaryOfEmployees_WhenException() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenThrow(new RuntimeException("---Test Exception---"));
        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("---Test Exception---");
                });

    }


    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(List.of("Punit","Rohit","Sumit"));

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    List<String> highestEarningEmployees = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {});
                    assertThat(highestEarningEmployees).hasSize(3);
                    assertThat(highestEarningEmployees)
                            .isEqualTo(List.of("Punit","Rohit","Sumit"));
                });
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames_WhenException() throws Exception {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenThrow(new RuntimeException("---Test Exception---"));
        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("---Test Exception---");
                });

    }

    @Test
    public void testCreateEmployee() throws Exception {
        String createJson = """
                {"name":"test","salary": 123.0,"age": 23}               
                """;
        when(employeeService.createEmployee(Mockito.any())).thenReturn(buildEmployee());

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(result -> {
                    Employee employee = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertThat(employee).isEqualToComparingFieldByField(buildEmployee());
                });
    }


    @Test
    public void testDeleteEmployeeById() throws Exception {
        when(employeeService.deleteEmployeeById(anyString())).thenReturn("Punit");

        mockMvc.perform(delete("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("Punit");
                });
    }


    @Test
    public void testDeleteEmployeeById_WhenException() throws Exception {
        when(employeeService.deleteEmployeeById(anyString())).thenThrow(new DataNotFoundException("No Data found for the given id = 2"));
        mockMvc.perform(delete("/api/v1/employee/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    assertThat(result.getResponse().getContentAsString()).isEqualTo("No Data found for the given id = 2");
                });

    }


    private List<Employee> buildEmployeeList() {
        return List.of(
                Employee.builder().id(1L).employeeName("Punit Jain").employeeAge(34).employeeSalary(10000d).profileImage("123").build(),
                Employee.builder().id(2L).employeeName("Rohit Mehta").employeeAge(35).employeeSalary(5000d).profileImage("456").build());
    }

    private Employee buildEmployee() {
        return Employee.builder().id(1L).employeeName("Punit Jain").employeeAge(34).employeeSalary(10000d).profileImage("123").build();
    }

}
