package com.cubix.extsearchbatch.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    NewsEntity findTop1ByTitle(String title);
}
