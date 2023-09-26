package com.choihyojeong.extsearchbatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NaverNewsRepository extends JpaRepository<NaverNewsItem, Long> {
    NaverNewsItem findByTitle(String title);
    List<NaverNewsItem> findTop10ByOrderByIdDesc();
}
