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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(properties = {"spring.config.location = classpath:application.yml"})
@Transactional
public class NewsBatchServiceTest {

    @InjectMocks
    private NewsBatchService newsBatchService;

    @Autowired
    private NaverNewsItemRepository newsRepo;

    @Value("${NAVER.CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${NAVER.CLIENT_SECRET}")
    private String CLIENT_SECRET;

    private final String KEYWORD = "주식";

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
    @DisplayName("적재_개수가_10개다")
    public void newsBatchTest() throws Exception {
        newsBatchService.naverApiTest();

        assertEquals(10, newsRepo.count(), "Failed to load 10 pieces of data.");
    }

    @Test
    @DisplayName("키워드가_포함되어있다")
    public void newsKeywordTest() throws Exception {
        newsBatchService.naverApiTest();
        List<NaverNewsItem> newsData = newsRepo.findAll();

        assertTrue(!newsData.isEmpty(), "*** NewsData is Empty ***");

        for (NaverNewsItem item : newsData) {
            assertTrue(item.getTitle().contains(KEYWORD) || item.getDescription().contains(KEYWORD), "*** ID:" + item.getId() + " => Keyword not included.");
            log.info("*** ID:" + String.valueOf(item.getId()) + " => Keyword included.");
        }
    }
}
