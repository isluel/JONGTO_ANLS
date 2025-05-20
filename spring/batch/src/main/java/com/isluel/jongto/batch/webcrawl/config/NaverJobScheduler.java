package com.isluel.jongto.batch.webcrawl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class NaverJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job crawlJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void naverCrawlRun() throws Exception{
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .toJobParameters();

        jobLauncher.run(crawlJob, jobParameters);
    }
}
