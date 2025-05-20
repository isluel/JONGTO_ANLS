package com.isluel.jongto.batch.stcok.config;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.itemProcessor.StockItemProcessor;
import com.isluel.jongto.batch.stcok.itemReader.StockItemReader;
import com.isluel.jongto.batch.stcok.itemWriter.StockItemWriter;
import com.isluel.jongto.batch.stcok.service.DataPortalStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
public class StockStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final StockItemReader stockItemReader;
    private final StockItemWriter stockItemWriter;
    private final DataPortalStockService dataPortalStockService;

    @Bean
    public Step stockStep() {
        return new StepBuilder("stockStep", jobRepository)
                .<Stock, List<DailyStockPrice>>chunk(1, transactionManager)
                .reader(stockItemReader)
                .processor(stockProcessor(null, null))
                .writer(stockItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public StockItemProcessor stockProcessor(
            @Value("#{jobParameters['sdate']}") String sdate,
            @Value("#{jobParameters['edate']}") String edate
    ) {
        return new StockItemProcessor(dataPortalStockService, sdate, edate);
    }

}
