package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable("id") long id) {
        EmployeeDto employeeDto = employeeService.findEmployeeById(id);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                .lastModified(employeeDto.lastModifiedAt().atZone(ZoneId.systemDefault()))
                .eTag(Integer.toString(employeeDto.version()))
                .body(employeeDto);
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
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") long id,
                                                      @RequestHeader(HttpHeaders.IF_MATCH) String ifMatch,
                                                      @RequestBody EmployeeDto command) {
        int incomingVersion = Integer.parseInt(ifMatch.replaceAll("\"", ""));
        EmployeeDto employeeDto = employeeService.findEmployeeById(id);
        if (incomingVersion == employeeDto.version()) {
            return ResponseEntity.ok(employeeService.updateEmployee(id, command));
        }
        else {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SuppressWarnings("unused")
    public void deleteEmployee(@PathVariable("id") long id) {
        employeeService.deleteEmployee(id);
    }

}
