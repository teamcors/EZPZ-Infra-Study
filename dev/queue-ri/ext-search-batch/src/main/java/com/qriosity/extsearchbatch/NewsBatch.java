package com.qriosity.extsearchbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsBatch {
    private final NaverNewsItemRepository newsRepo;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${secrets.naver.id}")
    private String CLIENT_ID;

    @Value("${secrets.naver.secret}")
    private String CLIENT_SECRET;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul") // test: */20 * * * * *  prod: 0 0 3 * * *
    public void fetch() {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.json")
                .queryParam("query","주식")
                .queryParam("display",10)
                .queryParam("start", 1)
                .queryParam("sort","date")
                .encode()
                .build()
                .toUri();

        NaverNewsResponseDto response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(createHttpHeaders()),
                NaverNewsResponseDto.class
        ).getBody();

        saveItems(response);
    }

    public void saveItems(NaverNewsResponseDto response) {
        List<NaverNewsItem> items = response.getItems();
        List<NaverNewsItem> newItems = new ArrayList<>();
        for (NaverNewsItem item : items) {
            NaverNewsItem data = newsRepo.findByTitle(item.getTitle());
            if (data != null) newItems.add(updateData(item, data));
            else newItems.add(item);
        }
        newsRepo.saveAll(newItems);
        log.info("뉴스 데이터 적재 완료");
    }

    private NaverNewsItem updateData(NaverNewsItem newItem, NaverNewsItem oldItem) {
//        // legacy code
//        if (newItem.getDescription().compareTo(oldItem.getDescription()) != 0)
//            oldItem.setDescription(newItem.getDescription());
//        if (newItem.getLink().compareTo(oldItem.getLink()) != 0)
//            oldItem.setLink(newItem.getLink());
//        if (newItem.getOriginallink().compareTo(oldItem.getOriginallink()) != 0)
//            oldItem.setOriginallink(newItem.getOriginallink());
//        if (newItem.getPubDate().compareTo(oldItem.getPubDate()) != 0)
//            oldItem.setPubDate(newItem.getPubDate());
        
        // 비교 로직 생략하여 최적화
        oldItem.setDescription(newItem.getDescription());
        oldItem.setLink(newItem.getLink());
        oldItem.setOriginallink(newItem.getOriginallink());
        oldItem.setPubDate(newItem.getPubDate());
        // oldItem.setUpdatedAt(LocalDateTime.now()); // 엔티티 어노테이션으로 대체
        return oldItem;
    }

    private HttpHeaders createHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", CLIENT_ID);
        headers.add("X-Naver-Client-Secret", CLIENT_SECRET);
        return headers;
    }
}
