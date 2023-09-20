package com.chaeeun.extsearchbatch.service;

import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import com.chaeeun.extsearchbatch.dto.NaverResultDto;
import com.chaeeun.extsearchbatch.repository.NaverNewsItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NewsBatchService {
    @Autowired NaverNewsItemRepository newsRepo;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${NAVER.CLIENT_ID}")
    private String clientId;

    @Value("${NAVER.CLIENT_SECRET}")
    private String clientSecret;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void naverApiTest() {
        // API 호출
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.json")
                .queryParam("query","주식")
                .queryParam("display",10)
                .queryParam("start", 1)
                .queryParam("sort","sim")
                .encode()
                .build()
                .toUri();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id",clientId)
                .header("X-Naver-Client-Secret",clientSecret)
                .build();

        ResponseEntity<NaverResultDto> searchList = restTemplate.exchange(req, NaverResultDto.class);

        NaverResultDto resultDto = searchList.getBody();

        List<NaverNewsItem> newsList = resultDto.getItems();

        List<NaverNewsItem> itemsToSave = new ArrayList<>();

        for(int i = 0; i < newsList.size(); i++) {
            NaverNewsItem newsData = newsList.get(i);
            NaverNewsItem DBdata = newsRepo.findByTitle(newsData.getTitle());

            if (DBdata == null) {
                itemsToSave.add(newsData);
                log.info("news data 적재 완료");
            }
            else {
                DBdata.setDescription(newsData.getDescription());
                DBdata.setLink(newsData.getLink());
                DBdata.setOriginallink(newsData.getOriginallink());
                DBdata.setPubDate(newsData.getPubDate());
                itemsToSave.add(DBdata);
                log.info("news data 갱신 완료");
            }
        }
        newsRepo.saveAll(itemsToSave);
    }
}
