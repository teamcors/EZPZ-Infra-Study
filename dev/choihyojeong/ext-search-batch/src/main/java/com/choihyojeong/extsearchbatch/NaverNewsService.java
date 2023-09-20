package com.choihyojeong.extsearchbatch;

import lombok.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class NaverNewsService {
    private final NaverNewsRepository NaverNewsRepo;

    //Spring 제공 restTemplate
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${naver.client.id}")
    private String CLIENT_ID;

    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;

    @Value("${naver.base.url}")
    private String BASE_URL;

    @Value("${naver.base.path}")
    private String PATH;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *",zone = "Asia/Seoul")
    public void naverNewsApi ()  {
        URI BUrl = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .path(PATH)
                .queryParam("query","주식")
                .queryParam("display",10)
                .queryParam("start",1)
                .queryParam("sort","date")
                .encode()
                .build()
                .toUri();


        MultiValueMap<String, String> httpConnect;
        NaverResultDTO resultDTO = restTemplate.exchange(
                BUrl, HttpMethod.GET,
                new HttpEntity<>(httpConnect()),
                NaverResultDTO.class
        ).getBody();

        List<NaverNewsItem> newsItem = resultDTO.getItems();
        List<NaverNewsItem> saveItem = new ArrayList<>();

        for (NaverNewsItem news : newsItem){ 
            
            NaverNewsItem findNews = NaverNewsRepo.findByTitle((news.getTitle())); 

            String newNews = news.getLink();


            if (findNews == null) { 
                saveItem.add(news);
                System.out.println("뉴스 데이터 적재 완료");
            }
            else {
                String oldNews = findNews.getLink();
                if(!oldNews.equals(newNews)){ 
                    findNews.setDescription(news.getDescription());
                    findNews.setLink(news.getLink());
                    findNews.setOriginallink(news.getOriginallink());
                    findNews.setPubDate(news.getPubDate());


                    saveItem.add(findNews);
                    System.out.println(findNews.getTitle() + ": 뉴스 데이터 업데이트 완료");
                }
                else 
                {
                    System.out.println(findNews.getTitle() + ": 최신 뉴스입니다.");
                }
            }
            NaverNewsRepo.saveAll(saveItem);
        }
    }

    //헤더 정보 세팅
    private MultiValueMap<String, String> httpConnect() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", CLIENT_ID);
        headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
        return headers;
    }

}
