package com.isluel.jongto.batch.stcok.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isluel.jongto.batch.stcok.domain.DailyStockPrice;
import com.isluel.jongto.batch.stcok.domain.DataGoApiResponse;
import com.isluel.jongto.batch.stcok.domain.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPortalStockService {

    private String BASE_URL = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService";
    private String STOCK_URL = "getStockPriceInfo";
    private String SERVICE_KEY_NAME = "serviceKey";
    @Value("${MY_API_KEY}")
    private String SERVICE_KEY;
    private String NUM_OF_ROWS_NAME = "numOfRows";
    private int NUM_OF_ROWS= 1;
    private String PAGE_NO_NAME = "pageNo";
    private int PAGE_NO = 1;
    private String RESULT_TYPE_NAME = "resultType";
    private String RESULT_TYPE = "json";
//    private String BASE_DATE_NAME = "basDt";
    private String BEGIN_DATE_NAME = "beginBasDt";
    private String End_DATE_NAME = "endBasDt";
    private String ITEM_NAME_NAME = "itmsNm";

    private final RestClient restClient = RestClient.create(BASE_URL);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // date = YYYY-MM-dd
    public List<DailyStockPrice> execute(Stock stock, LocalDate start, LocalDate end) throws Exception {
        List<DailyStockPrice> result = new ArrayList<>();
        String startString = start.format(formatter);
        String endString = end.format(formatter);

        String fullUrl = "/" + STOCK_URL + "?" + NUM_OF_ROWS_NAME + "=" + NUM_OF_ROWS
                + "&" + PAGE_NO_NAME + "=" + PAGE_NO + "&" + RESULT_TYPE_NAME + "=" + RESULT_TYPE
                + "&" + BEGIN_DATE_NAME + "=" + startString + "&" + End_DATE_NAME + "=" + endString
                + "&" + ITEM_NAME_NAME + "=" + stock.getName() + "&" + SERVICE_KEY_NAME + "=" + SERVICE_KEY;
        log.info("FULL URL: {}", fullUrl);

        var response = restClient.get()
                .uri(fullUrl)
                .retrieve()
                .body(String.class);

        var apiResponse = objectMapper.readValue(response, DataGoApiResponse.class);

        result = DailyStockPrice.makeFromResponse(apiResponse, stock.getId());

        return result;
    }
}
