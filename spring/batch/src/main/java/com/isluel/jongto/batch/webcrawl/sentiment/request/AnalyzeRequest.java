package com.isluel.jongto.batch.webcrawl.sentiment.request;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalyzeRequest {
    private Long id;
    private String sentence;

    public static AnalyzeRequest of(Long id, String sentence) {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest();
        analyzeRequest.id = id;
        analyzeRequest.sentence = sentence;
        return analyzeRequest;
    }

    public static AnalyzeRequest fromPost(NaverPost post) {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest();
        analyzeRequest.id = post.getId();
        analyzeRequest.sentence = post.getTitle() + "\n" + post.getContent();
        return analyzeRequest;
    }
}
