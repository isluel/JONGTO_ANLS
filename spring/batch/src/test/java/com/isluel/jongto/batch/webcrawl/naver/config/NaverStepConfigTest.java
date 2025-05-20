package com.isluel.jongto.batch.webcrawl.naver.config;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import com.isluel.jongto.batch.webcrawl.naver.itemProcessor.NaverItemListProcessor;
import com.isluel.jongto.batch.webcrawl.naver.itemReader.NaverItemReader;
import com.isluel.jongto.batch.webcrawl.naver.itemWriter.NaverItemWriter;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverStockRepository;
import com.isluel.jongto.batch.webcrawl.naver.service.NaverCrawlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NaverStepConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private NaverStockRepository naverStockRepository;
    @MockitoBean
    private NaverCrawlService naverCrawlService;
    @MockitoBean
    private NaverItemReader naverItemReader;
    @MockitoBean
    private NaverItemListProcessor naverItemListProcessor;
    @MockitoBean
    private NaverItemWriter naverItemWriter;

    @Autowired
    private Job crawlJob;

    @BeforeEach
    void setup() {
        jobLauncherTestUtils.setJob(crawlJob);
    }

    @DisplayName("Step을 실행하여 DB에 저장된 Naver Stock 을 가져와 Naver Post 로 만든다.")
    @Test
    void naverCrawlStep() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        LocalDate edate = LocalDate.of(2024,1,2);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("sdate", "2025-05-11")
                .addString("edate", "2025-05-12")
                .toJobParameters();
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
        naverStockRepository.save(stock);
        BDDMockito.given(naverCrawlService.loadPosts(stock, sdate, edate))
                .willReturn(List.of(naverPost1, naverPost2));
        BDDMockito.given(naverItemReader.read())
                        .willReturn(stock, null);
        BDDMockito.given(naverItemListProcessor.process(stock))
                .willReturn(List.of(naverPost1, naverPost2));

        // when
        var execution = jobLauncherTestUtils.launchStep("naverCrawlStep", jobParameters);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        // reader
        then(naverItemReader).should(times(2)).read();

        // processor
        BDDMockito.verify(naverItemListProcessor, times(1)).process(stock);

        // writer
        ArgumentCaptor<Chunk> captor = ArgumentCaptor.forClass(Chunk.class);
        BDDMockito.verify(naverItemWriter, times(1)).write(captor.capture());
        Chunk chunks = captor.getValue();
        List<NaverPost> list = (List<NaverPost>) chunks.getItems().getFirst();
        assertThat(list).hasSize(2)
                .contains(naverPost1, naverPost2);
    }
}