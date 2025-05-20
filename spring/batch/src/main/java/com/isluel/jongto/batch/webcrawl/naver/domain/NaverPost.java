package com.isluel.jongto.batch.webcrawl.naver.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "naver_post")
@NoArgsConstructor
public class NaverPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String writer;
    private LocalDateTime postDateTime;
    private String url;

    @Lob
    private String content;

    private int viewCount;
    private int goodCount;
    private int badCount;

    @Builder
    private NaverPost(Long id, String title, String content, String writer, String url, LocalDateTime postDateTime, int viewCount, int goodCount, int badCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.url = url;
        this.postDateTime = postDateTime;
        this.viewCount = viewCount;
        this.goodCount = goodCount;
        this.badCount = badCount;
    }
}
