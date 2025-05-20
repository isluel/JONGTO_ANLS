package com.isluel.jongto.batch.webcrawl.naver.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "naver_stock")
@NoArgsConstructor
public class NaverStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "stock_id")
    private Long stockId;
    @Column(name = "naver")
    private String name;
    @Column(name = "naver_code")
    private String naverCode;

    @Builder
    private NaverStock(String name, String naverCode) {
        this.name = name;
        this.naverCode = naverCode;
    }
}
