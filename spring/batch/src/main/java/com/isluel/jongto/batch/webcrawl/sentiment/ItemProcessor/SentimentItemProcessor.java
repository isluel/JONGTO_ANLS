package com.isluel.jongto.batch.webcrawl.sentiment.ItemProcessor;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.service.SentimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SentimentItemProcessor implements ItemProcessor<NaverPost, Sentiment> {
    private final SentimentService service;

    public SentimentItemProcessor(SentimentService service) {
        this.service = service;
    }

    @Override
    public Sentiment process(NaverPost item) {
        try {
            var response = service.execute(item);
            var sentiment = Sentiment.from(response);

            return sentiment;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

    }
}
