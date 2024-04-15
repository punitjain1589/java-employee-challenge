package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.ApiResponseException;
import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeDto;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.model.CreateEmployeeRequest;
import com.example.rqchallenge.model.Employee;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class EmployeeServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IEmployeeService employeeService;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetAllEmployees() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).hasSize(2);
        assertThat(employees)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(buildEmployeeList());
        verify(getRequestedFor(urlEqualTo("/api/v1/employees")));
    }


    @Test
    public void testGetAllEmployees_WhenApiCallFailed() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                )
        );

        assertThatThrownBy(() -> employeeService.getAllEmployees())
                .isInstanceOf(ApiResponseException.class)
                .hasMessageContaining("Failed to get the response from API");

        //It ensures retry is working
        verify(3,getRequestedFor(urlEqualTo("/api/v1/employees")));
    }


    @Test
    public void testGetAllEmployees_WhenApiCallSuccessAfterRetry() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();

        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/employees"))
                        .inScenario("Retry")
                        .willSetStateTo("Success After Retry")
                        .willReturn(aResponse().withStatus(500))
        );

        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/employees"))
                        .inScenario("Retry")
                        .whenScenarioStateIs("Success After Retry")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(Json.write(apiResponse))
                        )
        );

        List<Employee> employees = employeeService.getAllEmployees();

        assertThat(employees).hasSize(2);
        assertThat(employees)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(buildEmployeeList());
        // 1+1 = total 2 retries
        verify(2,getRequestedFor(urlEqualTo("/api/v1/employees")));
    }


    @Test
    public void testGetEmployeesByNameSearch() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        List<Employee> employees = employeeService.getEmployeesByNameSearch("Punit");

        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getEmployeeName()).isEqualTo("Punit Jain");
        assertThat(employees.get(0).getEmployeeSalary()).isEqualTo(10000);
        verify(getRequestedFor(urlEqualTo("/api/v1/employees")));
    }

    @Test
    public void testGetEmployeesByNameSearch_WhenApiCallFailed() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                )
        );

        assertThatThrownBy(() -> employeeService.getEmployeesByNameSearch("Punit"))
                .isInstanceOf(ApiResponseException.class)
                .hasMessageContaining("Failed to get the response from API");

        //It ensures retry is working
        verify(3,getRequestedFor(urlEqualTo("/api/v1/employees")));
    }

    @Test
    public void testGetEmployeesById() {
        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employee/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        Employee employees = employeeService.getEmployeeById("2");

        assertThat(employees).isNotNull();
        assertThat(employees.getEmployeeName()).isEqualTo("Rohit Mehta");
        assertThat(employees.getEmployeeSalary()).isEqualTo(5000);
        verify(getRequestedFor(urlEqualTo("/api/v1/employee/2")));
    }

    @Test
    public void testGetEmployeesById_WhenIdNotExist() {
        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();
        apiResponse.setData(null);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employee/25"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        assertThatThrownBy(() -> employeeService.getEmployeeById("25"))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("No Data found on the API for given selector");

        //It ensures retry is working
        verify(1,getRequestedFor(urlEqualTo("/api/v1/employee/25")));

    }

    @Test
    public void testGetHighestSalaryOfEmployees() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertThat(highestSalary).isNotNull();
        assertThat(highestSalary).isEqualTo(10000);
        verify(getRequestedFor(urlEqualTo("/api/v1/employees")));
    }

    @Test
    public void testGetHighestSalaryOfEmployees_WhenNoEmployeeFromApi() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();
        apiResponse.setData(new ArrayList<>());

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertThat(highestSalary).isNotNull();
        assertThat(highestSalary).isEqualTo(0);
        verify(getRequestedFor(urlEqualTo("/api/v1/employees")));
    }

    @Test
    public void testGetHighestSalaryOfEmployees_WhenApiCallFailed() {
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                )
        );

        assertThatThrownBy(() -> employeeService.getHighestSalaryOfEmployees())
                .isInstanceOf(TooManyRequestException.class)
                .hasMessageContaining("API is unable to take the request");

        //It ensures retry is working
        verify(3,getRequestedFor(urlEqualTo("/api/v1/employees")));
    }


    @Test
    public void testGetTopTenHighestEarningEmployeeNames() {
        EmployeeListApiResponse apiResponse = buildEmployeeListApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employees"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        List<String> highestEarningEmployess = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(highestEarningEmployess).isNotNull();
        assertThat(highestEarningEmployess).hasSize(2);
        assertThat(highestEarningEmployess.get(0)).isEqualTo("Punit Jain");
        assertThat(highestEarningEmployess.get(1)).isEqualTo("Rohit Mehta");
        verify(getRequestedFor(urlEqualTo("/api/v1/employees")));
    }

    @Test
    public void testCreateEmployee() {
        CreateEmployeeRequest createRequest = CreateEmployeeRequest.builder()
                                                        .name("Rohit Mehta")
                                                        .age(35)
                                                        .salary(5000d)
                                                        .build();

        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();


        wireMockServer.stubFor(post(urlEqualTo("/api/v1/create"))
                .withRequestBody(equalToJson(Json.write(createRequest)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        Employee employee = employeeService.createEmployee(createRequest);

        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isNotNull();
        assertThat(employee.getEmployeeName()).isEqualTo("Rohit Mehta");
        assertThat(employee.getEmployeeAge()).isEqualTo(35);
        assertThat(employee.getEmployeeSalary()).isEqualTo(5000);
        verify(postRequestedFor(urlEqualTo("/api/v1/create")));
    }

    @Test
    public void testCreateEmployee_WhenNameIsEmpty() {
        CreateEmployeeRequest createRequest = CreateEmployeeRequest.builder()
                .name(null)
                .age(5)
                .salary(5000d)
                .build();

        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();


        wireMockServer.stubFor(post(urlEqualTo("/api/v1/create"))
                .withRequestBody(equalToJson(Json.write(createRequest)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        assertThatThrownBy(() -> employeeService.createEmployee(createRequest))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("name: must not be empty");

        //It ensures retry is working
        verify(0,getRequestedFor(urlEqualTo("/api/v1/create")));
    }

    @Test
    public void testCreateEmployee_WhenAgeIsNegative() {
        CreateEmployeeRequest createRequest = CreateEmployeeRequest.builder()
                .name("Punit")
                .age(-1)
                .salary(5000d)
                .build();

        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();


        wireMockServer.stubFor(post(urlEqualTo("/api/v1/create"))
                .withRequestBody(equalToJson(Json.write(createRequest)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        assertThatThrownBy(() -> employeeService.createEmployee(createRequest))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("age: must be greater than 0");

        //It ensures retry is working
        verify(0,getRequestedFor(urlEqualTo("/api/v1/create")));
    }


    @Test
    public void testDeleteEmployeeById() {
        EmployeeApiResponse apiResponse = buildEmployeeApiResponse();

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/employee/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(apiResponse))
                )
        );

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/delete/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(Json.write(EmployeeApiResponse.builder().message("Record successfully deleted").status("success").build()))
                )
        );

        String deletedEmployeeName = employeeService.deleteEmployeeById("2");

        assertThat(deletedEmployeeName).isNotNull();
        assertThat(deletedEmployeeName).isEqualTo("Rohit Mehta");
        verify(getRequestedFor(urlEqualTo("/api/v1/employee/2")));
        verify(getRequestedFor(urlEqualTo("/api/v1/delete/2")));
    }

    private List<Employee> buildEmployeeList() {
        return List.of(
                Employee.builder().id(1L).employeeName("Punit Jain").employeeAge(34).employeeSalary(10000d).profileImage("123").build(),
                Employee.builder().id(2L).employeeName("Rohit Mehta").employeeAge(35).employeeSalary(5000d).profileImage("456").build());
    }

    private EmployeeListApiResponse buildEmployeeListApiResponse() {
        return EmployeeListApiResponse.builder()
                .status("success")
                .data(buildEmployeeDtoList())
                .message("Successfully! All records has been fetched.")
                .build();
    }

    private List<EmployeeDto> buildEmployeeDtoList() {
        return List.of(
                EmployeeDto.builder().id(1L).employeeName("Punit Jain").employeeAge(34).employeeSalary(10000).profileImage("123").build(),
                EmployeeDto.builder().id(2L).employeeName("Rohit Mehta").employeeAge(35).employeeSalary(5000).profileImage("456").build());
    }

    private EmployeeApiResponse buildEmployeeApiResponse() {
        return EmployeeApiResponse.builder()
                .status("success")
                .data(buildEmployeeDto())
                .message("Successfully! All records has been fetched.")
                .build();
    }

    private EmployeeDto buildEmployeeDto() {
        return EmployeeDto.builder().id(2L).employeeName("Rohit Mehta").employeeAge(35).employeeSalary(5000).profileImage("456").build();
    }

}
