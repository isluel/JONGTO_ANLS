package com.isluel.jongto.batch.webcrawl.sentiment.itemReader;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class SentimentItemReader extends JpaPagingItemReader<NaverPost> {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SentimentItemReader(EntityManagerFactory entityManagerFactory, String sdate, String edate) {
        this.startDate = LocalDate.parse(sdate, FORMATTER).atStartOfDay();
        this.endDate = LocalDate.parse(edate, FORMATTER).atStartOfDay();
        this.setName("naverItemReader");
        this.setEntityManagerFactory(entityManagerFactory);
        this.setQueryString("SELECT np FROM NaverPost np WHERE np.postDateTime >= :startDate AND np.postDateTime < :endDate");
        this.setParameterValues(Map.of("startDate", startDate, "endDate", endDate));
        this.setPageSize(10);
    }
}
