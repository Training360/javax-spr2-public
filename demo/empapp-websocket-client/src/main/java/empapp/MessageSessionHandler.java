package empapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
@Slf4j
public class MessageSessionHandler implements StompSessionHandler {

    private final CountDownLatch latch;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Connected");
        session.subscribe("/topic/employees", this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        throw new IllegalStateException("Handle exception", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        throw new IllegalStateException("Handle transport error", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ResponseMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload instanceof ResponseMessage responseMessage) {
            log.info("Received response message: {}", responseMessage);
        }
        latch.countDown();
    }
}
