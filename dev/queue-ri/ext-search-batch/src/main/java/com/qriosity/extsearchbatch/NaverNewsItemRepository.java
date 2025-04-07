package com.qriosity.extsearchbatch;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverNewsItemRepository extends JpaRepository<NaverNewsItem, Long> {
    NaverNewsItem findByTitle(String title);
}
