package empapp;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration(proxyBeanMethods = true)
public class QuartzConfiguration {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(EmployeeJob.class)
                .withIdentity("employeeJob")
                .withDescription("Count")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("employeeTrigger")
                .withDescription("Trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
    }
}
