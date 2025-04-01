package empapp;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class MessagesService {

    @SneakyThrows
    @Async
    public void sendMessages(SseEmitter emitter) {
        for (int i = 0; i < 5; i++) {
            emitter.send("Message " + i);
            Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
        }
        emitter.complete();
    }
}
