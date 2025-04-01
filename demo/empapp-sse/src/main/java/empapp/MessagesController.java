package empapp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/messages")
@Slf4j
@RequiredArgsConstructor
public class MessagesController {

    private final MessagesService messagesService;

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping
    @SneakyThrows
    public SseEmitter getMessages() {
        SseEmitter emitter = new SseEmitter();
//        messagesService.sendMessages(emitter);
        emitter.send("Connected");
        emitters.add(emitter);
        log.info("Emitter return");
        return emitter;
    }

    @EventListener
    public void handleEvent(EmployeeHasBeenCreatedEvent event) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder builder = SseEmitter
                        .event()
                        .name("message")
                        .comment("Employee has been created")
                        .id(UUID.randomUUID().toString())
                        .reconnectTime(10_000)
                        .data(event)
                        ;
                emitter.send(builder);
            }
            catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
