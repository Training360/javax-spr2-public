package empapp;

import empapp.dto.EmployeeHasBeenCreatedEvent;
import empapp.dto.RequestMessage;
import empapp.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    @SendTo("/topic/employees")
    public ResponseMessage sendMessage(RequestMessage message) {
        log.info("Request: {}", message);
        return new ResponseMessage("Reply: " + message.requestText());
    }

    @EventListener
    public void sendMessage(EmployeeHasBeenCreatedEvent event) {
        messagingTemplate.convertAndSend("/topic/employees",
                new ResponseMessage("Employee has been created: " + event.name()));
    }
}
