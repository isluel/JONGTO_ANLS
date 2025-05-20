package com.isluel.jongto.batch.webcrawl.naver.itemReader;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NaverItemReaderTest {

    @Autowired
    NaverStockRepository naverStockRepository;

    @Autowired
    NaverItemReader naverItemReader;

    @DisplayName("저장된 NaverStock 정보를 읽어온다.")
    @Test
    void naverItemReader() throws Exception {
        // given
        var post1 = NaverStock.builder().name("stock1").naverCode("123123").build();
        var post2 = NaverStock.builder().name("stock2").naverCode("159879").build();
        naverStockRepository.save(post1);
        naverStockRepository.save(post2);

        naverItemReader.open(new ExecutionContext());

        // when
        List<NaverStock> results = new ArrayList<>();
        NaverStock stock = null;
        while((stock = naverItemReader.read()) != null) {
            results.add(stock);
        }

        // then
        assertThat(results)
                .hasSize(2)
                .extracting("name", "naverCode")
                .containsExactly(
                        tuple(post1.getName(), post1.getNaverCode()),
                        tuple(post2.getName(), post2.getNaverCode())
                );
    }

}