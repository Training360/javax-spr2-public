package empapp;

import empapp.dto.AddressDto;
import empapp.dto.EmployeeDto;
import empapp.entity.Address;
import empapp.entity.Employee;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final AddressRepository addressRepository;

    private final EmployeeMapper employeeMapper;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public EmployeeDto saveEmployee(EmployeeDto command) {
        Employee employee = employeeMapper.toEmployee(command);
        employee = employeeRepository.save(employee);

        sendMail(employee);

        return employeeMapper.toEmployeeDto(employee);
    }

    @SneakyThrows
    public void sendMail(Employee employee) {
//        String text = "Dear Employee!";

        String text = templateEngine.process("mail",
                new Context(Locale.ENGLISH, Map.of("employee", employee)));

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo("employee@training.com");
        helper.setFrom("admin@training.com");
        helper.setSubject("Hello");
        helper.setText(text, true);

        mailSender.send(message);
    }

    public AddressDto saveAddress(long employeeId, AddressDto command) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(notFoundException(employeeId));
        Address address = employeeMapper.toAddress(command);
        address.setEmployee(employee);
        addressRepository.save(address);
        return employeeMapper.toAddressDto(address);
    }

    public List<EmployeeDto> findEmployees() {
        return employeeRepository.findAllBy(EmployeeDto.class);
    }

    public List<AddressDto> findAddresses(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(notFoundException(employeeId));
        return addressRepository.findAllByEmployee(employee, AddressDto.class);
    }

    public EmployeeDto findEmployeeById(long id) {
        return employeeRepository.findById(id, EmployeeDto.class)
                        .orElseThrow(notFoundException(id));
    }

    @Transactional
    public EmployeeDto updateEmployee(long id, EmployeeDto command) {
        Employee employeeToModify = employeeRepository
                .findById(id)
                .orElseThrow(notFoundException(id));
        employeeToModify.setName(command.name());
        return employeeMapper.toEmployeeDto(employeeToModify);
    }

    public void deleteEmployee(long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(notFoundException(id));
        employeeRepository.delete(employee);
    }

    private static Supplier<NotFoundException> notFoundException(long id) {
        return () -> new NotFoundException("Employee not found with id: " + id);
    }

}
