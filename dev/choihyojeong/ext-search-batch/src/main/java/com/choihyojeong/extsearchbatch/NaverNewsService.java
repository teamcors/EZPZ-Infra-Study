package com.choihyojeong.extsearchbatch;

import lombok.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


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

    @Value("${naver.base.path}")
    private String path;

    @Scheduled(cron = "0 0 3 * * *",zone = "Asia/Seoul")
    public void naverNewsApi ()  {
        URI BUrl = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(path)
                .queryParam("query","주식")
                .queryParam("display",10)
                .queryParam("start",1)
                .queryParam("sort","sim")
                .encode()
                .build()
                .toUri();

        //System.out.println(baseUrl);
        //System.out.println(BUrl);

        MultiValueMap<String, String> httpConnect;
        NaverResultVO resultVO = restTemplate.exchange(
                BUrl, HttpMethod.GET,
                new HttpEntity<>(httpConnect()),
                NaverResultVO.class
        ).getBody();

        List<NaverNewsItem> newsItem = resultVO.getItems();

        for (NaverNewsItem news : newsItem){ // NaverNews news는 지금 새로 리스트에 들어온거
            
            //System.out.println(news);
            NaverNewsItem findNews = NaverNewsRepo.findByTitle((news.getTitle())); //find news는 DB에서 찾아온 거

            String oldNews = findNews.getLink();
            String newNews = news.getLink();


            if (findNews == null) { //없으면 저장해주면 됨
                NaverNewsRepo.save(news);
                System.out.println("뉴스 데이터 적재 완료");
            }
            else { //--> 덮어쓰는 형식
                if(!oldNews.equals(newNews)){ //링크가 안 같을 때에는 업데이트 함
                    //System.out.println(news.getLink()+"  /  "+findNews.getLink());
                    //System.out.println(oldNews+"  /  "+newNews);
                    findNews.setDescription(news.getDescription());
                    findNews.setLink(news.getLink());
                    findNews.setOriginallink(news.getOriginallink());
                    findNews.setPubDate(news.getPubDate());

                    //pubDate로 진행했을 때에 mysql에서 pubDate 칼럼을 인식못하는 경우 발생 ->pub_Date로 해결
                    // 2023.9.13 다시 진행해보니 해결 -> pubDate로 수정
                    NaverNewsRepo.save(findNews);
                    System.out.println(findNews.getTitle() + ": 뉴스 데이터 업데이트 완료");
                }
                else //링크까지 같을 때에는 새로 업데이트 안함
                {
                    System.out.println(findNews.getTitle() + ": 최신 뉴스입니다.");
                }
            }
        }
    }

    //헤더 정보 세팅
    private MultiValueMap<String, String> httpConnect() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        return headers;
    }

}
