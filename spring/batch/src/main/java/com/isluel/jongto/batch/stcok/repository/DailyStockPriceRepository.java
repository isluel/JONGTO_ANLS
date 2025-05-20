package com.isluel.jongto.batch.stcok.repository;

import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStockPriceRepository extends JpaRepository<DailyStockPrice, Long> {
}
