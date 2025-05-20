package com.isluel.jongto.batch.stcok.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_stock_price")
@Getter
@ToString
public class DailyStockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long stockId;
    // 단축 코드 (종목 코드 보다 짧으면서 유일성이 보장되는 코드, 6자리)
    private String srtnCd;
    private String name;
    // 시장 구분( Kospi, kosdaq, konex 중 1)
    private String marketCategory;
    // 종가
    private double closingPrice;
    // 전일 대비 등락
    private double vsStock;
    // 전일 대비 등락 에 따른 비율(등락률)
    private String fluctuationRage;
    // 시가
    private double marketPrice;
    // 고가
    private double highPrice;
    // 저가
    private double lowPrice;
    // 거래량
    private double tradingVolume;
    // 상장 주식수
    private double sharesList;
    // 시가 총액. 종가 * 상장 주식수
    private double marketTotalAmount;

    public static DailyStockPrice create(Long id, Long stockId, String srtnCd, String name, String marketCategory, double closingPrice, double vsStock, double highPrice, double lowPrice, double tradingVolume, double sharesList, double marketTotalAmount ) {
        DailyStockPrice dailyStockPrice = new DailyStockPrice();
        dailyStockPrice.id = id;
        dailyStockPrice.stockId = stockId;
        dailyStockPrice.srtnCd = srtnCd;
        dailyStockPrice.name = name;
        dailyStockPrice.marketCategory = marketCategory;
        dailyStockPrice.closingPrice = closingPrice;
        dailyStockPrice.vsStock = vsStock;
        dailyStockPrice.highPrice = highPrice;
        dailyStockPrice.lowPrice = lowPrice;
        dailyStockPrice.tradingVolume = tradingVolume;
        dailyStockPrice.sharesList = sharesList;
        dailyStockPrice.marketTotalAmount = marketTotalAmount;

        return dailyStockPrice;
    }

    public static List<DailyStockPrice> makeFromResponse(DataGoApiResponse response, Long stockId) {
        List<DailyStockPrice> result = new ArrayList<>();
        var itemList = response.getResponse().getBody().getItems().getItem();

        for (var item : itemList) {
            result.add(make(item, stockId));
        }

        return result;
    }

    public static DailyStockPrice make(DataGoApiResponse.DataGoApiResponseItemContent content, Long stockId) {
        DailyStockPrice price = new DailyStockPrice();
        price.id = stockId;
        price.srtnCd = content.getSrtnCd();
        price.name = content.getItmsNm();
        price.marketCategory = content.getMrktCtg();
        price.closingPrice = Double.parseDouble(content.getClpr());
        price.vsStock = Double.parseDouble(content.getVs());
        price.fluctuationRage = content.getFltRt();
        price.marketPrice = Double.parseDouble(content.getMkp());
        price.highPrice = Double.parseDouble(content.getHipr());
        price.lowPrice = Double.parseDouble(content.getLopr());
        price.tradingVolume = Double.parseDouble(content.getTrqu());
        price.sharesList = Double.parseDouble(content.getLstgStCnt());
        price.marketTotalAmount = Double.parseDouble(content.getMrktTotAmt());

        return price;
    }
}
