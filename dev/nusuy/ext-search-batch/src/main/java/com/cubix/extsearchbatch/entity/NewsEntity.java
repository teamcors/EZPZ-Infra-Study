package com.cubix.extsearchbatch.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@DynamicUpdate
@Table(name = "naver_news_item")
@Entity
public class NewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String url;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public NewsEntity(String title, String description, String url, LocalDateTime publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    @Transactional
    public void update(NewsEntity entity) {
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.url = entity.getUrl();
        this.publishedAt = entity.getPublishedAt();
    }
}
