package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import empapp.entity.Address;
import empapp.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final AddressRepository addressRepository;

    private final EmployeeMapper employeeMapper;

    private final CacheManager cacheManager;

//    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeDto saveEmployee(EmployeeDto command) {
        Employee employee = employeeMapper.toEmployee(command);
        employee = employeeRepository.save(employee);

        cacheManager.getCache("employees").clear();

        return employeeMapper.toEmployeeDto(employee);
    }

    public AddressDto saveAddress(long employeeId, AddressDto command) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(notFoundException(employeeId));
        Address address = employeeMapper.toAddress(command);
        address.setEmployee(employee);
        addressRepository.save(address);
        return employeeMapper.toAddressDto(address);
    }

    @Cacheable("employees")
    public List<EmployeeDto> findEmployees() {
        return employeeRepository.findAllBy(EmployeeDto.class);
    }

    public List<AddressDto> findAddresses(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(notFoundException(employeeId));
        return addressRepository.findAllByEmployee(employee, AddressDto.class);
    }

    @Cacheable("employee")
    public EmployeeDto findEmployeeById(long id) {
        return employeeRepository.findById(id, EmployeeDto.class)
                        .orElseThrow(notFoundException(id));
    }

    @Transactional
//    @CacheEvict(value = "employee", key = "#id")
    @CachePut(value = "employee", key = "#id")
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeDto updateEmployee(long id, EmployeeDto command) {
        Employee employeeToModify = employeeRepository
                .findById(id)
                .orElseThrow(notFoundException(id));
        employeeToModify.setName(command.name());
        return employeeMapper.toEmployeeDto(employeeToModify);
    }

    @Caching(evict = {
            @CacheEvict("employee"),
            @CacheEvict(value = "employees", allEntries = true)
    })
    public void deleteEmployee(long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(notFoundException(id));
        employeeRepository.delete(employee);
    }

    private static Supplier<NotFoundException> notFoundException(long id) {
        return () -> new NotFoundException("Employee not found with id: " + id);
    }

}
