package com.isluel.jongto.batch.stcok.itemProcessor;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.service.DataPortalStockService;
import com.isluel.jongto.batch.webcrawl.naver.itemProcessor.NaverItemListProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StockItemProcessorTest {

    @MockitoBean
    DataPortalStockService dataPortalStockService;

    StockItemProcessor stockItemProcessor;

    @DisplayName("Item Reader부터 전달 받은 Stock 으로 공공데이터 포탈에 조회해 DailyStockPrice 로 반환한다.")
    @Test
    void process() throws Exception {
        // given
        stockItemProcessor = new StockItemProcessor(dataPortalStockService, "2024-01-01", "2024-01-02");
        var stock = Stock.create(1L, "오름테라퓨틱");
        var dailyStockPrice1 = DailyStockPrice.create(1L, 1L, "srtnCD1", "이름1", "mcpt", 1d, 1d, 1d, 1d, 1d, 1d, 1d);
        var dailyStockPrice2 = DailyStockPrice.create(2L, 1L, "srtnCD2", "이름2", "mcpt2", 2d, 2d, 2d, 2d, 2d, 2d, 2d);

        BDDMockito.given(dataPortalStockService.execute(stock, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2)))
                .willReturn(List.of(dailyStockPrice1, dailyStockPrice2));

        // when
        var result = stockItemProcessor.process(stock);

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("id", "srtnCd", "name")
                .contains(
                        tuple(1L, "srtnCD1", "이름1"),
                        tuple(2L, "srtnCD2", "이름2")
                );
    }

    @DisplayName("API 연동 오류시 빈 리스트를 반환한다.")
    @Test
    void processWithErrorApi() throws Exception {
        // given
        stockItemProcessor = new StockItemProcessor(dataPortalStockService, "2024-01-01", "2024-01-02");
        var stock = Stock.create(1L, "오름테라퓨틱");

        BDDMockito.given(dataPortalStockService.execute(any(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(Exception.class);

        // when
        var result = stockItemProcessor.process(stock);

        // then
        assertThat(result)
                .hasSize(0);
    }
}