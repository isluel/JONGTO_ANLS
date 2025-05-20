package com.isluel.jongto.batch.webcrawl.sentiment.ItemProcessor;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.SentimentLabelType;
import com.isluel.jongto.batch.webcrawl.sentiment.response.AnalyzeResponse;
import com.isluel.jongto.batch.webcrawl.sentiment.service.SentimentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SentimentItemProcessorTest {

    @MockitoBean
    SentimentService sentimentService;

    SentimentItemProcessor sentimentItemProcessor;

    @DisplayName("Item Reader에서 전달 받은 Naver Post을 사용하여 Sentiment 객체를 얻어온다.")
    @Test
    void process() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        AnalyzeResponse response = AnalyzeResponse.create(1L, "setnece", "설레는(기대하는)", 0.423);
        BDDMockito.given(sentimentService.execute(naverPost1))
                .willReturn(response);

        // when
        sentimentItemProcessor = new SentimentItemProcessor(sentimentService);
        var result = sentimentItemProcessor.process(naverPost1);

        // then
        assertThat(result)
                .extracting("naverStockId", "Label", "score")
                .containsExactly(1L, SentimentLabelType.EXCITED, 0.423);
    }

    @DisplayName("연동 API 에서 오류 발생시 null을 반환한다.")
    @Test
    void processWithErrorApi() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        BDDMockito.given(sentimentService.execute(naverPost1))
                .willThrow(Exception.class);

        // when
        sentimentItemProcessor = new SentimentItemProcessor(sentimentService);
        var result = sentimentItemProcessor.process(naverPost1);

        // then
        assertThat(result).isNull();
    }
}