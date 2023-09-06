package com.cubix.extsearchbatch.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    Boolean existsByTitleAndUrlAndPublishedAt(String title, String url, LocalDateTime publishedAt);
}
