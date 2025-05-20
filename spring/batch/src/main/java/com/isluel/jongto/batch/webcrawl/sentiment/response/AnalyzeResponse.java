package com.isluel.jongto.batch.webcrawl.sentiment.response;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalyzeResponse {
    private Long id;
    private String sentence;
    private String label;
    private Double score;

    public static AnalyzeResponse create(Long id, String sentence, String label, Double score) {
        AnalyzeResponse response = new AnalyzeResponse();
        response.id = id;
        response.sentence = sentence;
        response.label = label;
        response.score = score;
        return response;
    }
}
