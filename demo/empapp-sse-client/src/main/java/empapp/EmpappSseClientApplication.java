package empapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@Slf4j
public class EmpappSseClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EmpappSseClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CountDownLatch counter = new CountDownLatch(1);
		WebClient.create("http://localhost:8080/api")
				.get()
				.uri("/messages")
				.retrieve()
				.bodyToFlux(EmployeeHasBeenCreatedEvent.class)
				.take(2)
				.doOnNext(event -> log.info("Event: {}", event))
				.doOnComplete(counter::countDown)
				.onErrorContinue((t, o) -> log.error("Error: {}", o, t))
				.subscribe();
		log.info("Waiting");
		counter.await();
		log.info("End");
	}
}
