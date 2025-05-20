package com.isluel.jongto.batch.stcok.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "stock")
@ToString
@Getter
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    public static Stock create(Long id, String name) {
        Stock stock = new Stock();
        stock.id = id;
        stock.name = name;
        return stock;
    }
}
