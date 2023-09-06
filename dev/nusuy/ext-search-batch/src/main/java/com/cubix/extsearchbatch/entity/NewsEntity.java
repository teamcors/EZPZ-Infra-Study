package com.cubix.extsearchbatch.entity;

import com.cubix.extsearchbatch.dto.NaverNewsItemDto;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
@Table(name = "naver_news_item")
@Entity
public class NewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Size(max = 500)
    private String description;
    @Size(max = 500)
    private String url;
    private LocalDateTime publishedAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public NewsEntity(NaverNewsItemDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.url = dto.getLink();
        this.publishedAt = dto.getPubDate();
    }

    @Transactional
    public void update(NaverNewsItemDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.url = dto.getLink();
        this.publishedAt = dto.getPubDate();
    }
}
