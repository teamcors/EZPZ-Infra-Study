package com.cubix.extsearchbatch;

import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.entity.NewsRepository;
import com.cubix.extsearchbatch.service.DataUpdateService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

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
        long expectedRawSize = 1000;

        // when
        dataUpdateService.updateNewsData();

        // then
        ArrayList<NaverRawNewsItemDto> rawData = dataUpdateService.getRawData();
        ArrayList<NewsEntity> resultData = dataUpdateService.getResultData();

        assertThat(rawData.size()).isEqualTo(expectedRawSize);
        for (NewsEntity news : resultData) {
            assertThat(newsRepository.findTop1ByTitle(news.getTitle())).isNotNull();
        }
    }

    @Test
    @DisplayName("Query 테스트")
    public void queryTest() {
        // given
        String query = "주식";

        // when
        dataUpdateService.updateNewsData();

        // then
        ArrayList<NewsEntity> resultData = dataUpdateService.getResultData();
        for (NewsEntity news : resultData) {
            boolean isPassed = news.getTitle().contains(query) || news.getDescription().contains(query);
            assertThat(isPassed).isTrue();
        }
    }
}
