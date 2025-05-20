package com.isluel.jongto.batch.stcok.itemWriter;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.repository.DailyStockPriceRepository;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.itemWriter.NaverItemWriter;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockItemWriterTest {

    @Autowired
    private DailyStockPriceRepository dailyStockPriceRepository;
    @Autowired
    private StockItemWriter stockItemWriter;

    @DisplayName("ItemProcessor 로 부터 전달 받은 DailyStockPrice 를 DB에 저장한다")
    @Test
    void writer() throws Exception {
        // given
        var dailyStockPrice1 = DailyStockPrice
                .create(1L, 1L, "srtnCD1", "이름1", "mcpt", 1d, 1d, 1d, 1d, 1d, 1d, 1d);
        var dailyStockPrice2 = DailyStockPrice
                .create(2L, 1L, "srtnCD2", "이름2", "mcpt2", 2d, 2d, 2d, 2d, 2d, 2d, 2d);
        var chunk = new Chunk<List<DailyStockPrice>>(List.of(dailyStockPrice1, dailyStockPrice2));

        // when
        stockItemWriter.write(chunk);

        // then
        var result = dailyStockPriceRepository.findAll();
        assertThat(result)
                .hasSize(2)
                .extracting("id", "srtnCd", "name")
                .contains(
                        tuple(1L, "srtnCD1", "이름1"),
                        tuple(2L, "srtnCD2", "이름2")
                );
    }

    @DisplayName("Item Processor 로 부터 빈 배열 전달시 아무것도 하지 않는다.")
    @Test
    void writerWithNoItems() throws Exception {
        // given
        var chunk = new Chunk<List<DailyStockPrice>>();

        // when
        stockItemWriter.write(chunk);

        // then
        var result = dailyStockPriceRepository.findAll();
        assertThat(result)
                .hasSize(0);
    }
}