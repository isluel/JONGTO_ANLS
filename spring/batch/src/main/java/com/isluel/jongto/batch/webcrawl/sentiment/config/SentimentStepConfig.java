package com.isluel.jongto.batch.webcrawl.sentiment.config;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemProcessor.SentimentItemProcessor;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemWrtier.SentimentItemWriter;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.itemReader.SentimentItemReader;
import com.isluel.jongto.batch.webcrawl.sentiment.service.SentimentService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SentimentStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final SentimentItemWriter sentimentItemWriter;
    private final SentimentService sentimentService;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Step sentimentStep() {
        return new StepBuilder("sentimentStep", jobRepository)
                .<NaverPost, Sentiment>chunk(10, transactionManager)
                .reader(sentimentItemReader(null, null))
                .processor(sentimentItemProcessor())
                .writer(sentimentItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public SentimentItemReader sentimentItemReader(
            @Value("#{jobParameters['sdate']}") String sdate,
            @Value("#{jobParameters['edate']}") String edate
    ) {
        return new SentimentItemReader(entityManagerFactory, sdate, edate);
    }

    @Bean
    public SentimentItemProcessor sentimentItemProcessor() {
        return new SentimentItemProcessor(sentimentService);
    }
}
