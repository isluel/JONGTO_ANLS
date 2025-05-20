package com.isluel.jongto.batch.webcrawl.naver.repository;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverStockRepository extends JpaRepository<NaverStock, Long> {
}
