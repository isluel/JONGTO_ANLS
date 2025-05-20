package com.isluel.jongto.batch.webcrawl.sentiment.config;

import com.isluel.jongto.batch.stcok.itemProcessor.StockItemProcessor;
import com.isluel.jongto.batch.stcok.itemReader.StockItemReader;
import com.isluel.jongto.batch.stcok.itemWriter.StockItemWriter;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemProcessor.SentimentItemProcessor;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemWrtier.SentimentItemWriter;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.SentimentLabelType;
import com.isluel.jongto.batch.webcrawl.sentiment.itemReader.SentimentItemReader;
import com.isluel.jongto.batch.webcrawl.sentiment.repository.SentimentRepository;
import com.isluel.jongto.batch.webcrawl.sentiment.service.SentimentService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.batch.core.*;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
class SentimentStepConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private NaverPostRepository postRepository;
    @Autowired
    private SentimentRepository sentimentRepository;
    @MockitoBean
    private SentimentService sentimentService;
    @MockitoBean
    private SentimentItemReader sentimentItemReader;
    @MockitoBean
    private SentimentItemProcessor sentimentItemProcessor;
    @MockitoBean
    private SentimentItemWriter sentimentItemWriter;

    @Autowired
    private Job crawlJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(crawlJob);
    }

    @DisplayName("")
    @Test
    void sentimentStep() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        LocalDate edate = LocalDate.of(2024,1,2);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("sdate", "2024-01-01")
                .addString("edate", "2024-01-02")
                .toJobParameters();
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        postRepository.save(naverPost1);
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(sdate.atStartOfDay().plusHours(1))
                .viewCount(2).goodCount(200).badCount(52)
                .build();
        postRepository.save(naverPost2);
        Sentiment sentiment1 = Sentiment.create(1L, SentimentLabelType.EXCITED, 0.423);
        Sentiment sentiment2 = Sentiment.create(2L, SentimentLabelType.SAD, 0.323);

        BDDMockito.given(sentimentItemReader.read())
                .willReturn(naverPost1, naverPost2, null);
        BDDMockito.given(sentimentItemProcessor.process(naverPost1))
                .willReturn(sentiment1);
        BDDMockito.given(sentimentItemProcessor.process(naverPost2))
                .willReturn(sentiment2);

        // when
        var execution = jobLauncherTestUtils.launchStep("sentimentStep", jobParameters);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // reader
        then(sentimentItemReader).should(times(3)).read();

        // processor
        BDDMockito.verify(sentimentItemProcessor, times(1)).process(naverPost1);
        BDDMockito.verify(sentimentItemProcessor, times(1)).process(naverPost2);

        // writer
        ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(sentimentItemWriter, times(1)).write(captor.capture());
        Chunk chunks = captor.getValue();
        List<Sentiment> result = chunks.getItems();
        assertThat(result)
                .hasSize(2)
                .extracting("naverPostId", "label", "score")
                .contains(
                        Tuple.tuple(sentiment1.getNaverPostId(), sentiment1.getLabel(), sentiment1.getScore()),
                        Tuple.tuple(sentiment2.getNaverPostId(), sentiment2.getLabel(), sentiment2.getScore())
                );
    }
}