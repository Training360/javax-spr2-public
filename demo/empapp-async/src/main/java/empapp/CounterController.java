package empapp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CounterController {

    private final CounterService counterService;

//    @GetMapping("/api/count")
////    @SneakyThrows
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void count() {
//        counterService.count();
////        Future<Integer> future = counterService.count();
////        log.info("future");
////        int number = future.get(12, TimeUnit.SECONDS);
////        log.info("number: {}", number);
//    }

//    @GetMapping("/api/count")
//    public DeferredResult<Integer> count() {
//        DeferredResult<Integer> result = new DeferredResult<>();
//        counterService.count(result);
//        log.info("Count");
//        return result;
//    }

    @GetMapping("/api/count")
    public Callable<Integer> count() {
        return () -> {
            for (int i = 0; i < 10; i++) {
                log.info("Counter: {}", i + 1);
                Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
            }
            return 10;
        };
    }

    @PostMapping("/api/schedule")
    public void schedule() {
        counterService.schedule();
    }
}
