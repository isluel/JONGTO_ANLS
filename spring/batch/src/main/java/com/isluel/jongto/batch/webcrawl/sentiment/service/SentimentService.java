package com.isluel.jongto.batch.webcrawl.sentiment.service;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.sentiment.request.AnalyzeRequest;
import com.isluel.jongto.batch.webcrawl.sentiment.response.AnalyzeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SentimentService {

    private RestClient restClient = RestClient.create("http://localhost:8000");

    public List<AnalyzeResponse> execute(List<NaverPost> naverPost) {
        List<AnalyzeRequest> analyzeRequests = new ArrayList<>();
        for (NaverPost post : naverPost) {
            AnalyzeRequest analyzeRequest = AnalyzeRequest.fromPost(post);
            analyzeRequests.add(analyzeRequest);
        }

        var result = restClient.post()
                .uri("/analyze")
                .body(analyzeRequests)
                .retrieve()
                .body(new ParameterizedTypeReference<List<AnalyzeResponse>>() {});

        return result;
    }

    public AnalyzeResponse execute(NaverPost naverPost) throws Exception {
        List<AnalyzeRequest> analyzeRequests = new ArrayList<>();
        analyzeRequests.add(AnalyzeRequest.fromPost(naverPost));


        var result = restClient.post()
                .uri("/analyze")
                .body(analyzeRequests)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new IllegalArgumentException("클라이언트 오류 발생: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new IllegalStateException("서버 오류 발생: " + res.getStatusCode());
                })
                .body(new ParameterizedTypeReference<List<AnalyzeResponse>>() {});

        return result.getFirst();
    }
}
