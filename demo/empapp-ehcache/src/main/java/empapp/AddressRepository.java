package empapp;

import empapp.entity.Address;
import empapp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    <T> List<T> findAllByEmployee(Employee employee, Class<T> clazz);
}
