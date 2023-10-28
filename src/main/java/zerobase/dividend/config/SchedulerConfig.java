package zerobase.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    // schedule을 실행하는 thread는 단일이다.
    // 때문에 여러 스케줄을 처리할 때 동작 시간이 겹치면 원하는 시간에 동작하지 않을 수 있다.
    // 이를 해결하기 위해 schedule 처리를 위한 thread pool을 생성해 준다.
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors()); // core 개수로 설정
        scheduler.setThreadNamePrefix("Scheduler-thread-");
        scheduler.initialize();

        taskRegistrar.setTaskScheduler(scheduler);
    }
}
