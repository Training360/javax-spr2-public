package empapp.gateway;

import empapp.EmployeeHasBeenCreatedEvent;
import empapp.EmployeeService;
import empapp.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Gateway
@RequiredArgsConstructor
@Slf4j
public class JmsGateway {

    public static final String EMPLOYEES_EVENTS_TOPIC = "employees-events";
    private final JmsTemplate jmsTemplate;
    private final JmsTemplate pubsubJmsTemplate;
    private final EmployeeService employeeService;

    @Transactional
    @EventListener
    public void sendMessage(EmployeeHasBeenCreatedEvent event) {
//        for (int i = 0; i < 2; i++) {
        pubsubJmsTemplate.convertAndSend(EMPLOYEES_EVENTS_TOPIC,
                    event, message -> {
                        message.setStringProperty("odd", Boolean.toString(event.id() % 2 == 1));
                        return message;
                    });
//        }
//        throw new IllegalStateException("Test exception");
    }

    @JmsListener(destination = EMPLOYEES_EVENTS_TOPIC, subscription = "events",
            containerFactory = "pubsubJmsListenerContainerFactory"
    )
//    @JmsListener(destination = EMPLOYEES_EVENTS_QUEUE,
//        selector = "odd = 'true'")
    public void receiveMessage(EmployeeHasBeenCreatedEvent event, @Header String odd,
                               @Header(name = JmsHeaders.MESSAGE_ID) String messageId,
                               @Headers Map<String, Object> headers) {
        log.info("Header: {}, {}, {}", odd, messageId, headers);
        log.info("Received employee event: {}", event);

//        throw new IllegalStateException("Can not process message");
    }

//    @JmsListener(destination = "employees-request")
//    @SendTo("employees-response")
//    public EmployeeDto findEmployeeById(FindEmployeeByIdRequest request) {
//        log.info("Received employee request: {}", request);
//        return employeeService.findEmployeeById(request.id());
//    }

    @JmsListener(destination = "employees-request")
    public void findEmployeeById(FindEmployeeByIdRequest request) {
        log.info("Received employee request: {}", request);
        EmployeeDto employeeDto = employeeService.findEmployeeById(request.id());
        jmsTemplate.convertAndSend("employees-response", employeeDto,
                message -> {
            message.setJMSCorrelationID(Long.toString(request.id()));
            return message;
                });
    }
}
