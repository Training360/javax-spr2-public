package empapp;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmpappJmsClientApplication implements CommandLineRunner {

	private final JmsTemplate jmsTemplate;

	public static void main(String[] args) {
		SpringApplication.run(EmpappJmsClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		// language=json
//		String json = """
//				{"id": 40}
//				""";
//		Message response = jmsTemplate.sendAndReceive("employees-request",
//				session -> {
//					TextMessage request = session.createTextMessage(json);
//					request.setStringProperty("_type", "FindEmployeeById");
//					return request;
//				});
//
//		log.info("Response: {}, destination: {}", ((TextMessage) response).getText(),
//				response.getJMSDestination());

		long id = 40;
		jmsTemplate.convertAndSend("employees-request", new FindEmployeeByIdRequest(id));

		EmployeeDto employee = (EmployeeDto) jmsTemplate.receiveSelectedAndConvert("employees-response",
				"JMSCorrelationID = '%d'".formatted(id));
		log.info("Employee: {}", employee.name());
	}
}
