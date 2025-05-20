package com.isluel.jongto.batch.stcok.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockJobConfig {
    private final JobRepository jobRepository;

    private final Step stockStep;

    @Bean
    public Job stockJob() {
        return new JobBuilder("stockJob", jobRepository)
                .start(stockStep)
                .build();
    }
}
