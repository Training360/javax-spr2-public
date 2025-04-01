package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import empapp.entity.Address;
import empapp.entity.Employee;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    EmployeeDto toEmployeeDto(Employee employee);

    Employee toEmployee(EmployeeDto employee);

    AddressDto toAddressDto(Address address);

    Address toAddress(AddressDto address);

}
