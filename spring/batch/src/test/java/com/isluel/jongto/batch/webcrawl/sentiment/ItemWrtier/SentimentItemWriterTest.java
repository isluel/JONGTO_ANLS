package com.isluel.jongto.batch.webcrawl.sentiment.ItemWrtier;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.SentimentLabelType;
import com.isluel.jongto.batch.webcrawl.sentiment.repository.SentimentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
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
class SentimentItemWriterTest {

    @Autowired
    private SentimentRepository sentimentRepository;
    @Autowired
    private SentimentItemWriter sentimentItemWriter;


    @DisplayName("Item Processor로붜 전달 받은 Sentiment를 저장한다.")
    @Test
    void write() throws Exception {
        // given
        Sentiment sentiment = Sentiment.create(1L, SentimentLabelType.EXCITED, 0.423);
        var chunk = new Chunk<Sentiment>();
        chunk.add(sentiment);

        // when
        sentimentItemWriter.write(chunk);

        // then
        List<Sentiment> results = sentimentRepository.findAll();
        assertThat(results)
                .hasSize(1)
                .extracting("naverPostId", "label", "score")
                .containsExactly(
                        tuple(sentiment.getNaverPostId(), sentiment.getLabel(), sentiment.getScore())
                );
    }

    @DisplayName("Item Processor에서 Null이 넘어온 경우 아무것도 저장하지 않는다.")
    @Test
    void writeFromNull() throws Exception {
        // given
        var chunk = new Chunk<Sentiment>();

        // when
        sentimentItemWriter.write(chunk);

        // then
        List<Sentiment> results = sentimentRepository.findAll();
        assertThat(results)
                .hasSize(0);
    }
}