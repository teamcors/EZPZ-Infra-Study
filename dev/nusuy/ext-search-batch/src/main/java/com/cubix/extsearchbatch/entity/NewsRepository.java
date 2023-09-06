package com.cubix.extsearchbatch.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    @Query("select n from NewsEntity n " +
            "where trim(n.title) = trim(:title) " +
            "and n.description = :description " +
            "and n.url = :url " +
            "and n.publishedAt = :publishedAt " +
            "order by n.publishedAt desc limit 1")
    NewsEntity findByTitleAndUrlAndPublishedAtWithJPQL(String title, String url, String description, LocalDateTime publishedAt);
}
