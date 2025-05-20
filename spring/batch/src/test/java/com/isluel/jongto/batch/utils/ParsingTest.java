package com.isluel.jongto.batch.utils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ParsingTest {


    @DisplayName("숫자로 된 문자를 Parsing 하여 int로 리턴한다.")
    @Test
    void parseIntSafe() {
        // given
        String test1 = "123123";
        String test2 = "as123123";

        // when
        var result1 = Parsing.parseIntSafe(test1);
        var result2 = Parsing.parseIntSafe(test2);

        // then
        assertThat(result1)
                .isEqualTo(123123);
        assertThat(result2)
                .isEqualTo(-1);
    }

    @DisplayName("Title 에 있는 댓글수를 제거하여 리턴한다.")
    @Test
    void removeTitleCommentCount() {
        String title1 = "asdf [1]";
        String title2 = "444444";

        // when
        var parse1 = Parsing.removeTitleCommentCount(title1);
        var parse2 = Parsing.removeTitleCommentCount(title2);

        // then
        assertThat(parse1).isEqualTo("asdf");
        assertThat(parse2).isEqualTo("444444");
    }
}