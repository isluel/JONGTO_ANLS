package com.isluel.jongto.batch.stcok.config;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.itemProcessor.StockItemProcessor;
import com.isluel.jongto.batch.stcok.itemReader.StockItemReader;
import com.isluel.jongto.batch.stcok.itemWriter.StockItemWriter;
import com.isluel.jongto.batch.stcok.repository.DailyStockPriceRepository;
import com.isluel.jongto.batch.stcok.repository.StockRepository;
import com.isluel.jongto.batch.stcok.service.DataPortalStockService;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StockStepConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private StockRepository stockRepository;
    @MockitoBean
    private DataPortalStockService dataPortalStockService;
    @MockitoBean
    private StockItemReader stockItemReader;
    @MockitoBean
    private StockItemProcessor stockProcessor;
    @MockitoBean
    private StockItemWriter stockItemWriter;

    @Autowired
    private Job stockJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(stockJob);
    }

    @DisplayName("Step을 실행하여 DB에 저장된 Stock 을 가져와 DailyStockPrice 로 만든다.")
    @Test
    void stockStep() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        LocalDate edate = LocalDate.of(2024,1,2);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("sdate", "2024-01-01")
                .addString("edate", "2024-01-02")
                .toJobParameters();
        var stock = Stock.create(1L, "오름테라퓨틱");
        stockRepository.save(stock);
        var dailyStockPrice1 = DailyStockPrice.create(1L, 1L, "srtnCD1", "이름1", "mcpt", 1d, 1d, 1d, 1d, 1d, 1d, 1d);
        var dailyStockPrice2 = DailyStockPrice.create(2L, 1L, "srtnCD2", "이름2", "mcpt2", 2d, 2d, 2d, 2d, 2d, 2d, 2d);
        BDDMockito.given(dataPortalStockService.execute(stock, sdate, edate))
                .willReturn(List.of(dailyStockPrice1, dailyStockPrice2));
        BDDMockito.given(stockItemReader.read())
                .willReturn(stock, null);
        BDDMockito.given(stockProcessor.process(stock))
                .willReturn(List.of(dailyStockPrice1, dailyStockPrice2));

        // when
        var execution = jobLauncherTestUtils.launchStep("stockStep", jobParameters);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // reader
        then(stockItemReader).should(times(2)).read();

        // processor
        BDDMockito.verify(stockProcessor, times(1)).process(stock);

        // writer
        ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(stockItemWriter, times(1)).write(captor.capture());
        Chunk chunks = captor.getValue();
        List<DailyStockPrice> list = (List<DailyStockPrice>) chunks.getItems().getFirst();
        assertThat(list).hasSize(2)
                .contains(dailyStockPrice1, dailyStockPrice2);
    }
}