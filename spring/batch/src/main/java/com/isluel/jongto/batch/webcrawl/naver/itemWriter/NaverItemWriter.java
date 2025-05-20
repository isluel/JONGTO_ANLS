package com.isluel.jongto.batch.webcrawl.naver.itemWriter;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.repository.NaverPostRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverItemWriter implements ItemWriter<List<NaverPost>> {

    private final NaverPostRepository naverPostRepository;

    @Override
    public void write(Chunk<? extends List<NaverPost>> items) {
        List<NaverPost> flatList = items.getItems().stream()
                .flatMap(List::stream)
                .toList();
        naverPostRepository.saveAll(flatList);
    }
}
