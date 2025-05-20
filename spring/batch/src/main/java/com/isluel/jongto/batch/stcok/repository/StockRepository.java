package com.isluel.jongto.batch.stcok.repository;

import com.isluel.jongto.batch.stcok.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
