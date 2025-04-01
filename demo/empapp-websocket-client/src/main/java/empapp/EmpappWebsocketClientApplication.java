package empapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class EmpappWebsocketClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmpappWebsocketClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CountDownLatch latch = new CountDownLatch(2);
		WebSocketClient client = new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())));
		WebSocketStompClient stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSessionHandler handler = new MessageSessionHandler(latch);
		stompClient.connectAsync("ws://localhost:8080/websocket-endpoint", handler)
						.thenAcceptAsync(session -> {
							for (int i = 0; i < 2; i++) {
								session.send("/app/messages", new RequestMessage("Hello " + i));
							}
						});
		latch.await();
	}
}
