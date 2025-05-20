package com.isluel.jongto.batch.webcrawl.naver.service;

import com.isluel.jongto.batch.utils.Parsing;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import com.isluel.jongto.batch.webcrawl.naver.domain.NaverStock;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NaverCrawlService {

    private String POST_BASE_URL = "https://finance.naver.com/item/board.naver?code=%s&page=";
    private String CONTENT_BASE_URL = "https://finance.naver.com";

    DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public List<NaverPost> loadPosts(NaverStock naverStock, LocalDate sdate, LocalDate edate) throws Exception {
        var postBaseUrlWithCode = POST_BASE_URL.formatted(naverStock.getNaverCode());

        List<NaverPost> posts = new ArrayList<>();

        int page = 1;
        while(true) {
            String listUrl = postBaseUrlWithCode + page;
            Document doc = Jsoup.connect(listUrl).userAgent("Mozilla/5.0").get();
            Elements rows = doc.select("table.type2 tr");

            for (Element row : rows) {
                Elements cols = row.select("td");

                if (cols.size() < 6) continue;

                String dateTimeString = cols.get(0).text().trim();

                LocalDate rowDate = LocalDate.parse(dateTimeString.split(" ")[0], FORMAT);
                if (rowDate.isBefore(sdate) && !rowDate.isEqual(sdate)) return posts;
                if (rowDate.isAfter(edate) || rowDate.equals(edate)) continue;

                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);

                Element titleTd = row.selectFirst("td.title");
                String writer = cols.get(2).text().trim();
                int viewCount = Parsing.parseIntSafe(cols.get(3).text());
                int goodCount = Parsing.parseIntSafe(cols.get(4).text());
                int badCount = Parsing.parseIntSafe(cols.get(5).text());

                if (titleTd != null) {
                    String title = Parsing.removeTitleCommentCount(titleTd.text());

                    Element hrefEle = titleTd.selectFirst("a");
                    // 삭제된것
                    if (hrefEle == null) continue;
                    var href = hrefEle.attr("href");
                    String postUrl = CONTENT_BASE_URL + href;
                    String content = extractContent(postUrl);

                    posts.add(
                            NaverPost.builder()
                                    .postDateTime(dateTime)
                                    .title(title)
                                    .writer(writer)
                                    .viewCount(viewCount)
                                    .url(postUrl)
                                    .goodCount(goodCount)
                                    .badCount(badCount)
                                    .content(content)
                                    .build()
                    );
                }
            }
            page++;
        }

    }

    public String extractContent(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

            String content = Optional.ofNullable(doc.selectFirst("div#body"))
                    .map(Element::text).orElse("");

            return content;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}, 크롤링 실패", url, e);
            return null;
        }
    }
}
