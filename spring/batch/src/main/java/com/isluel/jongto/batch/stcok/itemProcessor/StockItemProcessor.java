package com.isluel.jongto.batch.stcok.itemProcessor;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.Stock;
import com.isluel.jongto.batch.stcok.service.DataPortalStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class StockItemProcessor implements ItemProcessor<Stock, List<DailyStockPrice>> {

    private final DataPortalStockService stockService;
    private final LocalDate start;
    private final LocalDate end;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StockItemProcessor(DataPortalStockService stockService, String start, String end) {
        this.stockService = stockService;
        this.start = LocalDate.parse(start, formatter);
        this.end = LocalDate.parse(end, formatter);
    }

    @Override
    public List<DailyStockPrice> process(Stock item) {
        try {
            return stockService.execute(item, start, end);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }

    }
}
