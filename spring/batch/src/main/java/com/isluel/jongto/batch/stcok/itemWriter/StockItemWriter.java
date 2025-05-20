package com.isluel.jongto.batch.stcok.itemWriter;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.repository.DailyStockPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockItemWriter implements ItemWriter<List<DailyStockPrice>> {

     private final DailyStockPriceRepository dailyStockPriceRepository;

    @Override
    public void write(Chunk<? extends List<DailyStockPrice>> items) throws Exception {
        List<DailyStockPrice> flatList = items.getItems().stream()
                .flatMap(List::stream)
                .toList();

        dailyStockPriceRepository.saveAll(flatList);
    }
}
