package com.isluel.jongto.batch.webcrawl.naver.repository;

import com.isluel.jongto.batch.webcrawl.naver.domain.NaverPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverPostRepository extends JpaRepository<NaverPost, Long> {
}
