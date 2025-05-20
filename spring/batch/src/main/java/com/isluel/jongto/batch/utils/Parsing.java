package com.isluel.jongto.batch.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Parsing {

    public static int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            log.error("{} parsing Error", text, e);
            return -1;
        }
    }

    // title에 있는 댓글 갯수를 삭제한다.
    public static String removeTitleCommentCount(String title) {
        return title.replaceAll("\\[\\d+]", "").trim();
    }

}
