package com.isluel.jongto.batch.webcrawl.sentiment.repository;

import com.isluel.jongto.batch.webcrawl.sentiment.domain.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentimentRepository extends JpaRepository<Sentiment, Long> {
}
