package com.isluel.jongto.batch.webcrawl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class crawlJobConfig {

    private final JobRepository jobRepository;

    private final Step naverCrawlStep;
    private final Step sentimentStep;

    @Bean
    public Job crawlJob() {
        return new JobBuilder("NaverCrawlJob", jobRepository)
                .start(naverCrawlStep)
                .next(sentimentStep)
                .build();
    }

}
