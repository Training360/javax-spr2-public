package empapp;

import empapp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    <T> List<T> findAllBy(Class<T> clazz);

    <T> Optional<T> findById(Long id, Class<T> clazz);

}
