package empapp.dto;

import java.io.Serializable;

public record EmployeeDto(Long id, String name) implements Serializable {

}
