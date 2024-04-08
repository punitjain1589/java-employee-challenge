package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.ApiResponseException;
import com.example.rqchallenge.exception.DataNotFoundException;
import com.example.rqchallenge.feign.dto.EmployeeApiResponse;
import com.example.rqchallenge.feign.dto.EmployeeDto;
import com.example.rqchallenge.feign.dto.EmployeeListApiResponse;
import com.example.rqchallenge.model.Employee;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
                .hasMessageContaining("Failed to fetch employees from API");

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
                .hasMessageContaining("Failed to fetch employees from API");

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

        Employee employees = employeeService.getEmployeesById("2");

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

        assertThatThrownBy(() -> employeeService.getEmployeesById("25"))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("No Data found on the API for given selector");

        //It ensures retry is working
        verify(1,getRequestedFor(urlEqualTo("/api/v1/employee/25")));

    }

    private List<Employee> buildEmployeeList() {
        return List.of(
                Employee.builder().id(1L).employeeName("Punit Jain").employeeAge(34).employeeSalary(10000).profileImage("123").build(),
                Employee.builder().id(2L).employeeName("Rohit Mehta").employeeAge(35).employeeSalary(5000).profileImage("456").build());
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
