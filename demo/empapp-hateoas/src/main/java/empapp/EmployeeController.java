package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @SuppressWarnings("unused")
    public CollectionModel<EmployeeDto> findEmployees() {
        List<EmployeeDto> employees = employeeService.findEmployees();
        for (EmployeeDto employee : employees) {
            employee.add(linkTo(methodOn(EmployeeController.class).findEmployeeById(employee.getId())).withSelfRel());
        }
        CollectionModel<EmployeeDto> model = CollectionModel.of(employees);
        model.add(linkTo(methodOn(EmployeeController.class).findEmployees()).withSelfRel());
        return model;
    }

    @GetMapping("/{employeeId}/addresses")
    public List<AddressDto> findAddresses(@PathVariable(name = "employeeId") long employeeId) {
        return employeeService.findAddresses(employeeId);
    }

    @GetMapping("/{id}")
    @SuppressWarnings("unused")
    public EmployeeDto findEmployeeById(@PathVariable("id") long id) {
        EmployeeDto employee = employeeService.findEmployeeById(id);
        employee.add(linkTo(methodOn(EmployeeController.class).findEmployeeById(id)).withSelfRel());
        return employee;
    }

    @PostMapping
    @SuppressWarnings("unused")
    public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto command) {
        EmployeeDto employeeDto = employeeService.saveEmployee(command);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(employeeDto.getId()).toUri()).body(employeeDto);
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
