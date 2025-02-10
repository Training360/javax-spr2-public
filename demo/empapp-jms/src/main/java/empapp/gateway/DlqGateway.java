package empapp.gateway;

import jakarta.annotation.PostConstruct;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Gateway
@RequiredArgsConstructor
@Slf4j
public class DlqGateway {

    private final JmsTemplate jmsTemplate;

    @PostConstruct
    public void listDlq() {
        List<String> messagesInDlq = jmsTemplate.browse("DLQ", (session, browser) -> {
           Enumeration enumeration = browser.getEnumeration();
           List<String> messages = new ArrayList<>();
           while (enumeration.hasMoreElements()) {
               Message message = (Message) enumeration.nextElement();
               String id = message.getJMSMessageID();
               String source = message.getStringProperty("_AMQ_ORIG_QUEUE");
               messages.add(id + ": " + source);
           }
           return messages;
        });
        log.info("Messages in DLQ: {}", messagesInDlq);
    }
}
