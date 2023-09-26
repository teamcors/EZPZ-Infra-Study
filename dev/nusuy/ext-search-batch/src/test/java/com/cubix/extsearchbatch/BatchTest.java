package com.cubix.extsearchbatch;

import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.util.data.NewsDataReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@DisplayName("ext-search-batch Test")
@SpringBootTest
public class BatchTest {
    private final NewsDataReader newsDataReader;
    final int DISPLAY_DEF = 100;

    @Autowired
    public BatchTest(NewsDataReader newsDataReader) {
        this.newsDataReader = newsDataReader;
    }

    @Test
    @DisplayName("적재 데이터량 테스트")
    public void quantityTest() {
        // given
        long expectedSize = 100;

        // when
        ArrayList<NaverRawNewsItemDto> items = newsDataReader.get(DISPLAY_DEF, 1).getItems();

        // then
        long size = items.size();
        assertThat(size).as("[Test Failed: 1,000 건 적재 실패] - actual: " + size).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("Query 테스트")
    public void queryTest() {
        // given
        String query = "주식";

        // when
        ArrayList<NaverRawNewsItemDto> items = newsDataReader.get(DISPLAY_DEF, 1).getItems();

        // then
        for (NaverRawNewsItemDto news : items) {
            boolean isPassed = news.getTitle().contains(query) || news.getDescription().contains(query);
            assertThat(isPassed).as("[Test failed: Query 미포함] - " + " / " + news.getTitle() + " / " + news.getDescription()).isTrue();
        }
    }
}
