package com.isluel.jongto.batch.webcrawl.naver.itemWriter;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NaverItemWriterTest {

    @Autowired
    private NaverPostRepository naverPostRepository;
    @Autowired
    private NaverItemWriter itemWriter;

    @DisplayName("ItemProcessor 로 부터 전달 받은 NaverPost 를 DB에 저장한다")
    @Test
    void naverItemWriter() {
        // given
        LocalDate sdate = LocalDate.of(2024,1,1);
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        NaverPost naverPost2 = NaverPost.builder()
                .title("제목2").url("url2").content("content2").writer("writer2").postDateTime(sdate.atStartOfDay().plusHours(1))
                .viewCount(2).goodCount(200).badCount(52)
                .build();
        var chunk = new Chunk<List<NaverPost>>();
        List<NaverPost> naverPosts = new ArrayList<>();
        naverPosts.add(naverPost1);
        chunk.add(naverPosts);

        // when
        itemWriter.write(chunk);

        // then
        List<NaverPost> result = naverPostRepository.findAll();
        assertThat(result).hasSize(1)
                .extracting("title", "url", "content", "writer", "postDateTime", "viewCount", "goodCount", "badCount")
                .containsExactly(
                        Tuple.tuple(naverPost1.getTitle(), naverPost1.getUrl(), naverPost1.getContent(), naverPost1.getWriter()
                                , naverPost1.getPostDateTime(), naverPost1.getViewCount(), naverPost1.getGoodCount(), naverPost1.getBadCount())
                );
    }

    @DisplayName("Item Prorcess 로부터 빈 List를 반환받으면 아무것도 하지 않는다.")
    @Test
    void itemWriterFromEmptyList() {
        LocalDate sdate = LocalDate.of(2024,1,1);
        NaverPost naverPost1 = NaverPost.builder()
                .title("제목1").url("url1").content("content1").writer("writer1").postDateTime(sdate.atStartOfDay()).viewCount(1).goodCount(100).badCount(50)
                .build();
        var chunk = new Chunk<List<NaverPost>>();
        List<NaverPost> naverPosts = new ArrayList<>();
        chunk.add(naverPosts);

        // when
        itemWriter.write(chunk);

        // then
        List<NaverPost> result = naverPostRepository.findAll();
        assertThat(result).hasSize(0);
    }
}