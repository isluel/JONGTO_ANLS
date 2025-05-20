package com.isluel.jongto.batch.webcrawl.config;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import com.isluel.jongto.batch.webcrawl.naver.itemProcessor.NaverItemListProcessor;
import com.isluel.jongto.batch.webcrawl.naver.itemReader.NaverItemReader;
import com.isluel.jongto.batch.webcrawl.naver.itemWriter.NaverItemWriter;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemProcessor.SentimentItemProcessor;
import com.isluel.jongto.batch.webcrawl.sentiment.ItemWrtier.SentimentItemWriter;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.SentimentLabelType;
import com.isluel.jongto.batch.webcrawl.sentiment.itemReader.SentimentItemReader;
import com.isluel.jongto.batch.webcrawl.sentiment.repository.SentimentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
public class crawlJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private Job crawlJob;
    @Autowired
    private NaverPostRepository postRepository;
    @Autowired
    private SentimentRepository sentimentRepository;

    @MockitoBean
    private NaverItemReader itemReader;
    @MockitoBean
    private NaverItemListProcessor naverItemListProcessor;
    @MockitoSpyBean
    private NaverItemWriter itemWriter;

    @MockitoBean
    private SentimentItemReader sentimentItemReader;
    @MockitoBean
    private SentimentItemProcessor sentimentItemProcessor;
    @MockitoSpyBean
    private SentimentItemWriter sentimentItemWriter;

    @DisplayName("crawl Job 을 실행하여 NaverCrawl, sentiment Step을 실행한다.")
    @Test
    void crawlJob() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        var stock = NaverStock.builder()
                .name("TEST1")
                .naverCode("12333")
                .build();
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(sdate.atStartOfDay().plusHours(1))
                .viewCount(2).goodCount(200).badCount(52)
                .build();
        BDDMockito.given(itemReader.read()).willReturn(stock)
                .willReturn(null); // 두번째 호출시 null 으로 종료
        BDDMockito.given(naverItemListProcessor.process(stock)).willReturn(Arrays.asList(naverPost1, naverPost2));

        Sentiment sentiment1 = Sentiment.create(1L, SentimentLabelType.EXCITED, 0.678);
        Sentiment sentiment2 = Sentiment.create(2L, SentimentLabelType.HAPPY, 0.123);
        BDDMockito.given(sentimentItemReader.read())
                .willReturn(naverPost1, naverPost2, null);
        BDDMockito.given(sentimentItemProcessor.process(naverPost1)).willReturn(sentiment1);
        BDDMockito.given(sentimentItemProcessor.process(naverPost2)).willReturn(sentiment2);

        // when
        jobLauncherTestUtils.setJob(crawlJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // writer 데이터 검증
        ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(itemWriter, times(1)).write(captor.capture());
        Chunk chunks = captor.getValue();
        List<NaverPost> postList = (List<NaverPost>) chunks.getItems().getFirst();
        assertThat(postList).hasSize(2)
                .contains(naverPost1, naverPost2);

        ArgumentCaptor<Chunk> captor2 = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(sentimentItemWriter, times(1)).write(captor2.capture());
        Chunk chunks2 = captor2.getValue();
        List<Sentiment> sentimentList = (List<Sentiment>) chunks2.getItems();
        assertThat(sentimentList).hasSize(2)
                .contains(sentiment1, sentiment2);

        // 저장 검증
        var postResult = postRepository.findAll();
        assertThat(postResult).hasSize(2)
                .extracting("title", "url", "content")
                .contains(
                        tuple(naverPost1.getTitle(), naverPost1.getUrl(), naverPost1.getContent()),
                        tuple(naverPost2.getTitle(), naverPost2.getUrl(), naverPost2.getContent())
                );

        var sentimentResult = sentimentRepository.findAll();
        assertThat(sentimentResult).hasSize(2)
                .extracting("naverPostId", "label", "score")
                .contains(
                        tuple(sentiment1.getNaverPostId(), sentiment1.getLabel(), sentiment1.getScore()),
                        tuple(sentiment2.getNaverPostId(), sentiment2.getLabel(), sentiment2.getScore())
                );
    }
}
