package com.isluel.jongto.batch.webcrawl.naver.itemReader;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

@Component
public class NaverItemReader extends JpaPagingItemReader<NaverStock> {

    public NaverItemReader(EntityManagerFactory entityManagerFactory) {
        this.setName("naverItemReader");
        this.setEntityManagerFactory(entityManagerFactory);
        this.setQueryString("SELECT ns FROM NaverStock ns");
        this.setPageSize(2);
    }
}
