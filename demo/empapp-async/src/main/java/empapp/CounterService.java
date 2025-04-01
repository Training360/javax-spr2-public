package empapp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CounterService {

    private final TaskExecutor taskExecutor;

    private final TaskScheduler taskScheduler;

//    @Async
//    @SneakyThrows
//    public CompletableFuture<Integer> count() {
//        for (int i = 0; i < 10; i++) {
//            log.info("Counter: {}", i + 1);
//            Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
//        }
//        return CompletableFuture.completedFuture(10);
////        throw new IllegalStateException("Test exception");
//    }

    public void count(DeferredResult<Integer> result) {
        taskExecutor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    log.info("Counter: {}", i + 1);
                    Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
                }
                result.setResult(10);
            } catch (Exception e) {
                log.error("Exception", e);
            }
        });
    }

//    @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "*/5 * * * * ?")
    public void log() {
        log.info("Log");
    }

    public void schedule() {
        taskScheduler.scheduleAtFixedRate(() -> {
            log.info("Schedule");
        }, Duration.of(5, ChronoUnit.SECONDS));
    }
}
