package com.isluel.jongto.batch.webcrawl.naver.itemProcessor;

import com.isluel.jongto.batch.webcrawl.naver.service.NaverCrawlService;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class NaverItemListProcessor implements ItemProcessor<NaverStock, List<NaverPost>> {

    private final NaverCrawlService service;
    private final LocalDate start;
    private final LocalDate end;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NaverItemListProcessor(NaverCrawlService service, String sdate, String edate) {
        this.service = service;
        this.start = LocalDate.parse(sdate, formatter);
        this.end = LocalDate.parse(edate, formatter);
    }

    @Override
    public List<NaverPost> process(NaverStock item) throws Exception {
        try {
            return service.loadPosts(item, start, end);
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }
}
