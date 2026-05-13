package com.turno.loanOriginationSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class ExecutorConfig {
    @Bean(name = "loanExecutor")
    public ThreadPoolTaskExecutor loanExecutor(){

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.initialize();
        return executor;
    }


}
