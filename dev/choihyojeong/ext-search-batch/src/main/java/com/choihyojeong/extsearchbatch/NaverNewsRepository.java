package com.choihyojeong.extsearchbatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverNewsRepository extends JpaRepository<NaverNewsItem, Long> {
    NaverNewsItem findByTitle(String title);
}
