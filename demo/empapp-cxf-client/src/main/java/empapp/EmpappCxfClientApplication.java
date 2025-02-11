package empapp;

import com.training.employees.EmployeeWdto;
import https.training_com.employees.EmployeeEndpointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
public class EmpappCxfClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmpappCxfClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Hello World");

		var service = new EmployeeEndpointService();
		var port = service.getEmployeeEndpointPort();
		var response = port.findAll();

		log.info("Employees: {}", response.stream().map(EmployeeWdto::getName).collect(Collectors.joining(", ")));

	}
}
