package empapp.dto;

import java.time.LocalDateTime;

public record EmployeeDto(Long id, String name, LocalDateTime lastModifiedAt, int version) {

}
