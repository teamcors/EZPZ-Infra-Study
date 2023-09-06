package com.chaeeun.extsearchbatch.repository;

import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsInfoRepository extends JpaRepository<NaverNewsItem,Long> {
    NaverNewsItem findByTitle(String title);
}
