package com.isluel.jongto.batch.webcrawl.naver.config;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import com.isluel.jongto.batch.webcrawl.naver.itemProcessor.NaverItemListProcessor;
import com.isluel.jongto.batch.webcrawl.naver.itemReader.NaverItemReader;
import com.isluel.jongto.batch.webcrawl.naver.itemWriter.NaverItemWriter;
import com.isluel.jongto.batch.webcrawl.naver.service.NaverCrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class NaverStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final NaverItemReader itemReader;
    private final NaverItemWriter itemWriter;
    private final NaverCrawlService service;

    @Bean
    public Step naverCrawlStep() {
        return new StepBuilder("naverCrawlStep", jobRepository)
                .<NaverStock, List<NaverPost>>chunk(1, transactionManager)
                .reader(itemReader)
                .processor(naverItemListProcessor(null, null))
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public NaverItemListProcessor naverItemListProcessor(
            @Value("#{jobParameters['sdate']}") String sdate,
            @Value("#{jobParameters['edate']}") String edate
    ) {
        return new NaverItemListProcessor(service, sdate, edate);
    }
}
