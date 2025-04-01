package empapp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@AllArgsConstructor
@Slf4j
public class EmployeeJob extends QuartzJobBean {

    private final EmployeeRepository employeeRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        long count = employeeRepository.count();
        log.info("Count: {}", count);
    }
}
