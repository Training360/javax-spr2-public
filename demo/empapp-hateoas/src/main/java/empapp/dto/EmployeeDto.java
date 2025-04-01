package empapp.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(value = "employee", collectionRelation = "employees")
public class EmployeeDto extends RepresentationModel<EmployeeDto> {

    private final Long id;
    private final String name;

    public EmployeeDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }


}
