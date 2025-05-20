package com.isluel.jongto.batch.webcrawl.sentiment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum SentimentLabelType {
    HAPPY(SentimentLabel.HAPPY_LABEL),
    GRATEFUL(SentimentLabel.GRATEFUL_LABEL),
    EXCITED(SentimentLabel.EXCITED_LABEL),
    LOVING(SentimentLabel.LOVING_LABEL),
    PLEASANT(SentimentLabel.PLEASANT_LABEL),
    ORDINARY(SentimentLabel.ORDINARY_LABEL),
    THOUGHTFUL(SentimentLabel.THOUGHTFUL_LABEL),
    SAD(SentimentLabel.SAD_LABEL),
    EXHAUSTED(SentimentLabel.EXHAUSTED_LABEL),
    ANNOYED(SentimentLabel.ANNOYED_LABEL),
    WORRIED(SentimentLabel.WORRIED_LABEL),
    ;

    private final String label;

    public static SentimentLabelType from(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (Exception e) {
            log.error("[SentimentLabelType.from] type = {}", type, e);
            return null;
        }
    }

    public static SentimentLabelType fromLabel(String label) {
        for (SentimentLabelType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        log.error("[SentimentLabelType.fromLabel] Unknown label = {}", label);
        return null;
    }

    public static class SentimentLabel {
        public static String HAPPY_LABEL = "기쁨(행복한)";
        public static String GRATEFUL_LABEL = "고마운";
        public static String EXCITED_LABEL = "설레는(기대하는)";
        public static String LOVING_LABEL = "사랑하는";
        public static String PLEASANT_LABEL = "즐거운(신나는)";
        public static String ORDINARY_LABEL = "일상적인";
        public static String THOUGHTFUL_LABEL = "생각이 많은";
        public static String SAD_LABEL = "슬픔(우울한)";
        public static String EXHAUSTED_LABEL = "힘듦(지침)";
        public static String ANNOYED_LABEL = "짜증남";
        public static String WORRIED_LABEL = "걱정스러운(불안한)";
    }
}
