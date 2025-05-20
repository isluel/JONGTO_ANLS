package com.isluel.jongto.batch.webcrawl.sentiment.service;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SentimentServiceTest {
    @Autowired
    private SentimentService sentimentService;

    @DisplayName("Naver Post을 Python API의 감정 분석을 요청한다.")
    @Test
    void execute() {
        // given
        NaverPost post1 = NaverPost.builder()
                .id(1L)
                .title("지금 열심히")
                .content("공포분위기 조성하느라 바쁜데 얘처럼 재무양호하고 저평가된 바이오주 하나 찾아서 가져와봐!")
                .build();
        NaverPost post2 = NaverPost.builder()
                .id(2L)
                .title("hlb주주들도")
                .content("어려움을 이겨냈는데 오름 주주분들도 이겨낼까라 생각합니다")
                .build();

        // when
        var result = sentimentService.execute(List.of(post1, post2));

        // then
        System.out.println(result);
        assertThat(result)
                .hasSize(2)
                .extracting("id", "sentence", "label")
                .containsExactly(
                        tuple(post1.getId(), post1.getTitle() + "\n" + post1.getContent(), "짜증남"),
                        tuple(post2.getId(), post2.getTitle() + "\n" + post2.getContent(), "걱정스러운(불안한)")
                );
    }
}