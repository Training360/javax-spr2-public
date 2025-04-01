package empapp;

import empapp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        RevisionRepository<Employee, Long, Long> {

    <T> List<T> findAllBy(Class<T> clazz);

    <T> Optional<T> findById(Long id, Class<T> clazz);

}
