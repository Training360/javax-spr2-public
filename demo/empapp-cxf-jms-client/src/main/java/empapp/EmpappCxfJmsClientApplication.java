package empapp;

import com.training.employees.EmployeeWdto;
import https.training_com.employees.EmployeeEndpoint;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmpappCxfJmsClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmpappCxfJmsClientApplication.class, args);
	}

//	private final JmsTemplate jmsTemplate;

	private final ConnectionFactory connectionFactory;

	@Override
	public void run(String... args) throws Exception {
//		// language=xml
//		String xml = """
//				<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:emp="https://training.com/employees">
//				                   <soapenv:Header/>
//				                   <soapenv:Body>
//				                      <emp:findAll/>
//				                   </soapenv:Body>
//				                </soapenv:Envelope>
//				""";
//
//		Message response = jmsTemplate.sendAndReceive("employees-ws-request", session -> {
//					TextMessage request = session.createTextMessage(xml);
//					request.setStringProperty("SOAPJMS_contentType", "application/soap+xml");
//					request.setStringProperty("SOAPJMS_requestURI", "jms:queue:employees-ws-request");
//					return request;
//				}
//				);
//
//		if (response instanceof TextMessage textMessage) {
//			log.info("Message: {}", textMessage.getText());
//		}

		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
		factoryBean.setServiceClass(EmployeeEndpoint.class);
		factoryBean.setAddress("jms:queue:employees-ws-request");

		JMSConfigFeature jmsConfigFeature = new JMSConfigFeature();

		JMSConfiguration jmsConfiguration = new JMSConfiguration();
		jmsConfiguration.setConnectionFactory(connectionFactory);
		jmsConfiguration.setTargetDestination("employees-ws-request");
		jmsConfiguration.setMessageType("text");
		jmsConfiguration.setRequestURI("jms:queue:employees-ws-request");

		jmsConfigFeature.setJmsConfig(jmsConfiguration);

		factoryBean.setFeatures(List.of(jmsConfigFeature));
		EmployeeEndpoint employeeEndpoint = (EmployeeEndpoint) factoryBean.create();

		List<EmployeeWdto> employees = employeeEndpoint.findAll();

		log.info("Employees: {}", employees.stream().map(EmployeeWdto::getName).collect(Collectors.joining(", ")));
	}
}
