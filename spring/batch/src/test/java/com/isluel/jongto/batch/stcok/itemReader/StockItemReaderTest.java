package com.isluel.jongto.batch.stcok.itemReader;

import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.repository.StockRepository;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import com.isluel.jongto.batch.webcrawl.naver.itemReader.NaverItemReader;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverStockRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockItemReaderTest {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockItemReader stockItemReader;

    @DisplayName("저장된 Stock 정보를 ItemReader가 읽어온다.")
    @Test
    void stockItemReaderTest() throws Exception {
        // given
        Stock stock1 = Stock.create(1L, "오름테라퓨틱");
        Stock stock2 = Stock.create(2L, "삼성전자");
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        // when
        stockItemReader.open(new ExecutionContext());
        List<Stock> results = new ArrayList<>();
        Stock stockTemp = null;
        while((stockTemp = stockItemReader.read()) != null) {
            results.add(stockTemp);
        }

        // then
        assertThat(results)
                .hasSize(2)
                .extracting("id", "name")
                .contains(
                        tuple(1L, "오름테라퓨틱"),
                        tuple(2L, "삼성전자")
                );

    }

}