package com.isluel.jongto.batch.webcrawl.naver.service;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@ActiveProfiles("test")
class NaverCrawlServiceTest {

    @Autowired
    private NaverCrawlService service;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @DisplayName("Naver 종토방에서 가져온 날짜를 Parsing 한다.")
    @Test
    void localDateParse() {
        String date = "2025.05.12";

        System.out.println(LocalDate.parse(date, formatter));
    }

    @DisplayName("Naver 종토방 게시글에서 글 내용을 가져온다.")
    @Test
    void extractContent() {
        // given
        String url = "https://finance.naver.com/item/board_read.naver?code=475830&nid=302016926&st=&sw=&page=1";

        // when
        var content = service.extractContent(url);

        // then
        assertThat(content).isNotNull()
                .contains("오름과의 파트너십");
    }


    @DisplayName("Naver API 를 통해 데이터를 정상적으로 가져온다.")
    @Test
    void loadPosts() throws Exception {
        // given
        NaverStock naverStock = NaverStock.builder()
                .name("오름테라퓨틱")
                .naverCode("475830")
                .build();
        LocalDate sdate = LocalDate.of(2025,5,11);
        LocalDate edate = LocalDate.of(2025,5,12);

        // when
        var posts = service.loadPosts(naverStock, sdate, edate);

        assertThat(posts).isNotNull()
                .hasSize(7);

        assertThat(posts.get(0))
                .extracting("title", "postDateTime", "goodCount", "badCount")
                .containsExactly("그리 잘하믄 돈 많이 벌엇나....", "2025.05.11 20:31", 3, 1);

        assertThat(posts.get(0).getContent()).isNotNull()
                .contains("돈 번넘은 입딱고 암말도 안하던데 딴데가서 놀아라 아그야..");
    }

}