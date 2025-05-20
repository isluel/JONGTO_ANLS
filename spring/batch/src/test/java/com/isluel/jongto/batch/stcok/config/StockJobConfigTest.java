package com.isluel.jongto.batch.stcok.config;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.itemProcessor.StockItemProcessor;
import com.isluel.jongto.batch.stcok.itemReader.StockItemReader;
import com.isluel.jongto.batch.stcok.itemWriter.StockItemWriter;
import com.isluel.jongto.batch.stcok.repository.DailyStockPriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class StockJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private Job stockJob;
    @Autowired
    DailyStockPriceRepository dailyStockPriceRepository;

    @MockitoBean
    private StockItemReader stockItemReader;
    @MockitoBean
    private StockItemProcessor stockItemProcessor;
    @MockitoSpyBean
    private StockItemWriter stockItemWriter;

    @DisplayName("Stock Job을 실행하여 Stock Step을 실행한다.")
    @Test
    void stockJob() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        LocalDate edate = LocalDate.of(2024,1,2);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("sdate", "2024-01-01")
                .addString("edate", "2024-01-02")
                .toJobParameters();
        var stock = Stock.create(1L, "오름테라퓨틱");
        var dailyStockPrice1 = DailyStockPrice.create(1L, 1L, "srtnCD1", "이름1", "mcpt", 1d, 1d, 1d, 1d, 1d, 1d, 1d);
        var dailyStockPrice2 = DailyStockPrice.create(2L, 1L, "srtnCD2", "이름2", "mcpt2", 2d, 2d, 2d, 2d, 2d, 2d, 2d);
        BDDMockito.given(stockItemReader.read())
                .willReturn(stock, null);
        BDDMockito.given(stockItemProcessor.process(stock))
                .willReturn(List.of(dailyStockPrice1, dailyStockPrice2));

        // when
        jobLauncherTestUtils.setJob(stockJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // writer
        ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(stockItemWriter, Mockito.times(1))
                .write(captor.capture());
        Chunk chunk = captor.getValue();
        List<DailyStockPrice> dailyStockPrices = (List<DailyStockPrice>) chunk.getItems().getFirst();
        assertThat(dailyStockPrices).hasSize(2)
                .contains(dailyStockPrice1, dailyStockPrice2);
    }
}