package com.isluel.jongto.batch.webcrawl.sentiment.ItemWrtier;

import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import com.isluel.jongto.batch.webcrawl.sentiment.repository.SentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SentimentItemWriter implements ItemWriter<Sentiment> {

    private final SentimentRepository sentimentRepository;

    @Override
    public void write(Chunk<? extends Sentiment> chunk) throws Exception {
        var list = chunk.getItems().stream()
                .toList();
        sentimentRepository.saveAll(list);
    }
}
