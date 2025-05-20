package com.isluel.jongto.batch.stcok.service;

import com.isluel.jongto.batch.stcok.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataPortalStockServiceTest {

    @InjectMocks
    private DataPortalStockService dataPortalStockService;

    @DisplayName("공공 데이터 포탈에서 데이터를 가져온다.")
    @Test
    void execute() throws Exception {
        // given
        Stock stock = Stock.create(1L, "오름테라퓨틱");
        LocalDate start = LocalDate.of(2025, 5, 12);
        LocalDate end = LocalDate.of(2025, 5, 16);

        // when
        var result = dataPortalStockService.execute(stock, start, end);

        // then
        assertThat(result)
                .hasSize(1)
                .extracting("name", "closingPrice", "marketPrice")
                .containsExactly(
                        tuple("오름테라퓨틱", 21150, 21250)
                );
    }

}