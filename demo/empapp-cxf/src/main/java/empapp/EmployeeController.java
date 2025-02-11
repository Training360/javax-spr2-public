package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @SuppressWarnings("unused")
    public List<EmployeeDto> findEmployees() {
        return employeeService.findEmployees();
    }

    @GetMapping("/{employeeId}/addresses")
    public List<AddressDto> findAddresses(@PathVariable(name = "employeeId") long employeeId) {
        return employeeService.findAddresses(employeeId);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("unused")
    public EmployeeDto findEmployeeById(@PathVariable("id") long id) {
        return employeeService.findEmployeeById(id);
    }

    @PostMapping
    @SuppressWarnings("unused")
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto command) {
        EmployeeDto employeeDto = employeeService.saveEmployee(command);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(employeeDto.id()).toUri()).body(employeeDto);
    }

    @PostMapping("/{employeeId}/addresses")
    @SuppressWarnings("unused")
    public ResponseEntity<AddressDto> saveAddress(@PathVariable("employeeId") long employeeId, @RequestBody AddressDto command) {
        AddressDto addressDto = employeeService.saveAddress(employeeId, command);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(addressDto.id()).toUri()).body(addressDto);
    }

    @PutMapping("/{id}")
    @SuppressWarnings("unused")
    public EmployeeDto updateEmployee(@PathVariable("id") long id, @RequestBody EmployeeDto command) {
        return employeeService.updateEmployee(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SuppressWarnings("unused")
    public void deleteEmployee(@PathVariable("id") long id) {
        employeeService.deleteEmployee(id);
    }

}
