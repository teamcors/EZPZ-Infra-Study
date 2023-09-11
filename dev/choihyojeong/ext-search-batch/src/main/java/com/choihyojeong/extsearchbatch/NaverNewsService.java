package com.choihyojeong.extsearchbatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.nio.charset.CharacterCodingException;


@RequiredArgsConstructor
@Service
public class NaverNewsService {
    private final NaverNewsRepository NaverNewsRepo;

    //Spring 제공 restTemplate
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.base.url}")
    private String baseUrl;

    @Scheduled(cron = "0 0 3 * * *",zone = "Asia/Seoul")
    public void naverNewsApi ()  {
        String BUrl = baseUrl
                + "?query=" +"%EC%A3%BC%EC%8B%9D&"
                + "&display=" + 10
                + "&start=" + 1
                + "&sort=" + "sim";

        //System.out.println(baseUrl);
        //System.out.println(BUrl);

        //URI 사용해서 진행했을 때 요청이 안갔음 변환시켜줌
        MultiValueMap<String, String> createHttpHeaders;
        NaverResultVO resultVO = restTemplate.exchange(
                BUrl, HttpMethod.GET,
                new HttpEntity<>(createHttpHeaders()),
                NaverResultVO.class
        ).getBody();

        List<NaverNewsItem> newsItem = resultVO.getItems();

        for (NaverNewsItem news : newsItem){ // NaverNews news는 지금 새로 리스트에 들어온거
            NaverNewsItem findNews = NaverNewsRepo.findByTitle((news.getTitle())); //DB에서 찾아온 거
            if (findNews == null) { //없으면 저장해주면 됨
                NaverNewsRepo.save(news);
                System.out.println("뉴스 데이터 적재 완료");
            }
            else { //그냥 교체해주기 --> 덮어쓰는 형식
                findNews.setDescription(news.getDescription());
                findNews.setLink(news.getLink());
                findNews.setOriginallink(news.getOriginallink());
                findNews.setPub_date(news.getPub_date()); //pubDate로 진행했을 때에 mysql에서 pubDate 칼럼을 인식못하는 경우 발생 ->pub_Date로 해결
                NaverNewsRepo.save(findNews);
                System.out.println(findNews.getTitle() + "뉴스 데이터 업데이트 완료");
            }
        }
    }

    //헤더 정보 세팅
    private MultiValueMap<String, String> createHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        return headers;
    }

}
