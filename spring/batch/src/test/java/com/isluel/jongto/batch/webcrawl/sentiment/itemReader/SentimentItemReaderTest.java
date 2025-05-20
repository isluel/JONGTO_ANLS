package com.isluel.jongto.batch.webcrawl.sentiment.itemReader;

import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
class SentimentItemReaderTest {

    @Autowired
    private NaverPostRepository naverPostRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @DisplayName("DB로부터 Post 데이터를 읽어온다.")
    @Test
    void read() throws Exception {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(sdate.atStartOfDay().plusHours(1))
                .viewCount(2).goodCount(200).badCount(52)
                .build();
        naverPostRepository.save(naverPost1);
        naverPostRepository.save(naverPost2);

        String start = "2024-01-01";
        String end = "2024-01-02";

        // when
        SentimentItemReader sentimentItemReader = new SentimentItemReader(entityManagerFactory, start, end);
        sentimentItemReader.open(new ExecutionContext());
        List<NaverPost> results = new ArrayList<>();
        NaverPost postTemp = null;
        while((postTemp = sentimentItemReader.read()) != null) {
            results.add(postTemp);
        }

        // then
        assertThat(results)
                .hasSize(2)
                .extracting("title", "url", "content")
            .contains(
                    tuple(naverPost1.getTitle(), naverPost1.getUrl(), naverPost1.getContent()),
                    tuple(naverPost2.getTitle(), naverPost2.getUrl(), naverPost2.getContent())
            );
    }
}