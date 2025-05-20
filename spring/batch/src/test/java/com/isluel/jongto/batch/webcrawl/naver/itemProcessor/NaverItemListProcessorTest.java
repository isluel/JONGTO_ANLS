package com.isluel.jongto.batch.webcrawl.naver.itemProcessor;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import com.isluel.jongto.batch.webcrawl.naver.service.NaverCrawlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NaverItemListProcessorTest {

    NaverItemListProcessor naverItemListProcessor;
    @MockitoBean
    NaverCrawlService naverCrawlService;

    @DisplayName("Item Reader부터 전달 받은 Naver Stock 으로 크롤링하여 Naver Post 로 반환한다.")
    @Test
    void process() throws Exception {
        // given
        naverItemListProcessor = new NaverItemListProcessor(naverCrawlService, "2024-01-01", "2024-01-02");
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        var naverStock = NaverStock.builder()
                .name("TEST1")
                .naverCode("12333")
                .build();
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(localDateTime).viewCount(1).goodCount(100).badCount(50)
                .build();
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(localDateTime.plusHours(1)).viewCount(2).goodCount(200).badCount(52)
                .build();
        BDDMockito.given(naverCrawlService.loadPosts(naverStock, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2)))
                .willReturn(List.of(naverPost1, naverPost2));

        // when
        var result = naverItemListProcessor.process(naverStock);

        // then
        assertThat(result).isNotNull()
                .hasSize(2)
                .contains(naverPost1, naverPost2);
    }

    @DisplayName("API 연동 오류시 빈 List를 반환한다.")
    @Test
    void processWithErrorAPI() throws Exception {
        // given
        naverItemListProcessor = new NaverItemListProcessor(naverCrawlService, "2024-01-01", "2024-01-02");
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        var naverStock = NaverStock.builder()
                .name("TEST1")
                .naverCode("12333")
                .build();
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(localDateTime).viewCount(1).goodCount(100).badCount(50)
                .build();
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(localDateTime.plusHours(1)).viewCount(2).goodCount(200).badCount(52)
                .build();
        BDDMockito.given(naverCrawlService.loadPosts(any(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(Exception.class);

        // when
        var result = naverItemListProcessor.process(naverStock);

        // then
        assertThat(result)
                .hasSize(0);
    }
}