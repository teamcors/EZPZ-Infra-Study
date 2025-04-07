package com.choihyojeong.extsearchbatch;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class NaverNewsItem {
    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키 하나 씩 증가
    @Column(name = "id", nullable = false)
    private Long id;

    @Column // length = 200
    private String title;

    @Column
    private String originallink;

    @Column
    private String link;

    @Column //length = 500
    private String description;

    private String pubDate;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated_at;
}
