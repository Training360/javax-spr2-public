package empapp.webservice;

import empapp.EmployeeService;
import empapp.wdto.EmployeeWdto;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static empapp.webservice.EmployeeEndpoint.EMPLOYEES_NAMESPACE;

@WebService(targetNamespace = EMPLOYEES_NAMESPACE)
@Service
@RequiredArgsConstructor
public class EmployeeEndpoint {

    public static final String EMPLOYEES_NAMESPACE = "https://training.com/employees";

    private final EmployeeService employeeService;

    @WebResult(name = "employee", targetNamespace = EMPLOYEES_NAMESPACE)
    public List<EmployeeWdto> findAll() {
        return employeeService.findEmployeeWdtos();
    }
}
