package com.cubix.extsearchbatch;

import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.entity.NewsRepository;
import com.cubix.extsearchbatch.service.DataUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@DisplayName("ext-search-batch Test")
@SpringBootTest
public class BatchTest {
    private final DataUpdateService dataUpdateService;
    private final NewsRepository newsRepository;

    @Autowired
    public BatchTest(DataUpdateService dataUpdateService, NewsRepository newsRepository) {
        this.dataUpdateService = dataUpdateService;
        this.newsRepository = newsRepository;
    }

    @Test
    @DisplayName("적재 데이터량 테스트")
    public void quantityTest() {
        // given
        long expectedSize = 1000;

        // when
        dataUpdateService.updateNewsData();

        // then
        long size = newsRepository.count();
        assertThat(size).as("[Test Failed: 1,000 건 적재 실패] - actual: " + size).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Query 테스트")
    public void queryTest() {
        // given
        String query = "주식";

        // when
        dataUpdateService.updateNewsData();

        // then
        List<NewsEntity> newsEntityList = newsRepository.findAll();
        for (NewsEntity news : newsEntityList) {
            boolean isPassed = news.getTitle().contains(query) || news.getDescription().contains(query);
            assertThat(isPassed).as("[Test failed: Query 미포함] - " + news.getTitle() + " / " + news.getTitle() + " / " + news.getDescription()).isTrue();
        }
    }
}
