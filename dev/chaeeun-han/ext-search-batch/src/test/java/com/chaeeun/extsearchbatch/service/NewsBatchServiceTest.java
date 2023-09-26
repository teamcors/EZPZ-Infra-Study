package com.chaeeun.extsearchbatch.service;

import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import com.chaeeun.extsearchbatch.repository.NaverNewsItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(properties = {"spring.config.location = classpath:application.yml"})
public class NewsBatchServiceTest {

    @InjectMocks
    private NewsBatchService newsBatchService;

    @Autowired
    private NaverNewsItemRepository newsRepo;

    @Value("${NAVER.CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${NAVER.CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // clientId와 clientSecret를 newsBatchService에 주입
        ReflectionTestUtils.setField(newsBatchService, "CLIENT_ID", CLIENT_ID);
        ReflectionTestUtils.setField(newsBatchService, "CLIENT_SECRET", CLIENT_SECRET);

        // newsRepo를 Mock 객체로 초기화
        newsBatchService.newsRepo = newsRepo;
    }


    @Test
    @Rollback(false)
    @DisplayName("적재_개수가_10개다")
    public void newsBatchTest() throws Exception {
        // 현재 데이터베이스에 저장된 행 수를 조회
        long currentRowCount = newsRepo.count();

        newsBatchService.naverApiTest();

        // 예상하는 값(현재 행 수 + 10)과 현재 데이터베이스의 행 수 비교
        assertEquals(currentRowCount + 10, newsRepo.count(), "Failed to load 10 pieces of data.");
    }

    @Test
    @DisplayName("주식_단어가_포함되어있다")
    public void newsKeywordTest() throws Exception {
        List<NaverNewsItem> newsData = newsRepo.findAll();

        assertTrue(!newsData.isEmpty(), "*** NewsData is Empty ***");

        for (NaverNewsItem item : newsData) {
            assertTrue(item.getTitle().contains(KEYWORD) || item.getDescription().contains(KEYWORD), "*** ID:" + item.getId() + " => Keyword not included.");
            log.info("*** ID:" + String.valueOf(item.getId()) + " => Keyword included.");
        }
    }
}
