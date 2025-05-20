package com.isluel.jongto.batch.stcok.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DataGoApiResponse {
    private DataGoApiResponseResult response;

    @Data
    public static class DataGoApiResponseResult {
        private DataGoApiResponseHeader header;
        private DataGoApiResponseBody body;
    }

    @Data
    public static class DataGoApiResponseHeader {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class DataGoApiResponseBody {
        private int numOfRows;
        private int pageNo;
        private int totalCount;
        private DataGoApiResponseItem items;
    }

    @Data
    public static class DataGoApiResponseItem {
        private List<DataGoApiResponseItemContent> item;
    }

    // {"basDt":"20250516","srtnCd":"475830","isinCd":"KR7475830006","itmsNm":"오름테라퓨틱","mrktCtg":"KOSDAQ","clpr":"21150","vs":"150","fltRt":".71","mkp":"21250"
    //      ,"hipr":"21700","lopr":"20350","trqu":"513106","trPrc":"10840268400","lstgStCnt":"21069082","mrktTotAmt":"445611084300"}
    @Data
    public static class DataGoApiResponseItemContent {
        private String basDt;
        private String srtnCd;
        private String isinCd;
        private String itmsNm;
        private String mrktCtg;
        private String clpr;
        private String vs;
        private String fltRt;
        private String mkp;
        private String hipr;
        private String lopr;
        private String trqu;
        private String trPrc;
        private String lstgStCnt;
        private String mrktTotAmt;
    }
}


