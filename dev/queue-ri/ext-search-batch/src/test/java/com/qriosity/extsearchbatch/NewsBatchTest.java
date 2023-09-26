package com.qriosity.extsearchbatch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
public class NewsBatchTest {

    @Autowired
    NaverNewsItemRepository newsRepo;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${secrets.naver.id}")
    private String CLIENT_ID;

    @Value("${secrets.naver.secret}")
    private String CLIENT_SECRET;

    private final String KEYWORD = "주식";

    @Test
    @DisplayName("신규 뉴스 데이터 적재 테스트")
    void saveTest() {
        // given
        NewsBatch batch = new NewsBatch(newsRepo);
        NaverNewsResponseDto response = getResponse();

        // when
        List<NaverNewsItem> items = response.getItems();
        List<NaverNewsItem> newItems = new ArrayList<>();
        for (NaverNewsItem item : items) {
            NaverNewsItem data = newsRepo.findByTitle(item.getTitle());
            if (data == null) newItems.add(item);
        }

        // repository는 생성자로 주입했지만
        // 배치 내부의 client id, secret은 주입이 안되므로 분리함
        batch.saveItems(response);

        // then
        for (NaverNewsItem newItem : newItems) {
            String title = newItem.getTitle();
            NaverNewsItem newData = newsRepo.findByTitle(title);
            if(newData == null)
                Assertions.fail("다음에 대한 레코드가 없습니다: " + title); // test fail
        }

        log.info("적재 완료: 신규(" + newItems.size() + ")");
        for (NaverNewsItem newItem : newItems)
            log.info("- title: " + newItem.getTitle() + ", pub_date: " + newItem.getPubDate());
    }

    @Test
    @DisplayName("키워드 포함 여부 테스트")
    void checkKeyword() {
        // given, when
        NewsBatch batch = new NewsBatch(newsRepo);
        NaverNewsResponseDto response = getResponse();

        // then
        List<NaverNewsItem> items = response.getItems();
        for (NaverNewsItem item : items) {
            if (!item.getTitle().contains(KEYWORD) && !item.getDescription().contains(KEYWORD))
                Assertions.fail("다음의 응답 데이터에 키워드가 없습니다: " + item.getTitle()); // test fail
        }

        log.info("키워드 검사 완료: 성공(" + items.size() + ")");
    }


    URI getUri() {
        return UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.json")
                .queryParam("query", KEYWORD)
                .queryParam("display",10)
                .queryParam("start", 1)
                .queryParam("sort","date")
                .encode()
                .build()
                .toUri();
    }

    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", CLIENT_ID);
        headers.add("X-Naver-Client-Secret", CLIENT_SECRET);
        return headers;
    }

    NaverNewsResponseDto getResponse() {
        return restTemplate.exchange(
                getUri(),
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                NaverNewsResponseDto.class
        ).getBody();
    }
}
