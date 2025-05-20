package com.isluel.jongto.batch.webcrawl.sentiment.domain;

import com.isluel.jongto.batch.webcrawl.sentiment.response.AnalyzeResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "sentiment")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sentiment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long naverPostId;
    @Enumerated(EnumType.STRING)
    private SentimentLabelType Label;
    private Double score;

    public static Sentiment create(Long naverPostId, SentimentLabelType label, Double score) {
        Sentiment sentiment = new Sentiment();
        sentiment.naverPostId = naverPostId;
        sentiment.Label = label;
        sentiment.score = score;
        return sentiment;
    }

    public static Sentiment create(Long id, Long naverPostId, SentimentLabelType label, Double score) {
        Sentiment sentiment = new Sentiment();
        sentiment.id = id;
        sentiment.naverPostId = naverPostId;
        sentiment.Label = label;
        sentiment.score = score;
        return sentiment;
    }

    public static Sentiment from(AnalyzeResponse response) {
        Sentiment sentiment = new Sentiment();
        sentiment.naverPostId = response.getId();
        sentiment.Label = SentimentLabelType.fromLabel(response.getLabel());
        sentiment.score = response.getScore();
        return sentiment;
    }
}
