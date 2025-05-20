package com.isluel.jongto.batch.stcok.itemReader;

import com.isluel.jongto.batch.stcok.domain.Stock;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

@Component
public class StockItemReader extends JpaPagingItemReader<Stock> {

    public StockItemReader(EntityManagerFactory entityManagerFactory) {
        this.setName("stockItemReader");
        this.setEntityManagerFactory(entityManagerFactory);
        this.setQueryString("SELECT s FROM Stock s");
        this.setPageSize(2);
    }
}
