package com.chaeeun.extsearchbatch.service;

import com.chaeeun.extsearchbatch.repository.NewsInfoRepository;
import com.chaeeun.extsearchbatch.vo.NaverResultVO;
import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.CharacterCodingException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestTemplateService {
    private final NewsInfoRepository newsRepo;

    @Value("${NAVER.CLIENT_ID}")
    private String clientId;

    @Value("${NAVER.CLIENT_SECRET}")
    private String clientSecret;

    public String naverApiTest() throws CharacterCodingException {
        System.out.println("clientId " + " / clientSecret ");
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

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> searchList = restTemplate.exchange(req, String.class);

        // JSON 파싱 (Json 문자열을 객체로 만듦, 문서화)
        ObjectMapper om = new ObjectMapper();
        NaverResultVO resultVO = null;

        try {
            resultVO = om.readValue(searchList.getBody(), NaverResultVO.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<NaverNewsItem> news =resultVO.getItems();

        for(int i=0;i<resultVO.getItems().size();i++){
            NaverNewsItem DBdata = newsRepo.findByTitle(news.get(i).getTitle());

            if (DBdata == null) {
                newsRepo.save(news.get(i));
                System.out.println("news data 적재 완료");
            }
            else {
                DBdata.setDescription(news.get(i).getDescription());
                DBdata.setLink(news.get(i).getLink());
                DBdata.setOriginallink(news.get(i).getOriginallink());
                DBdata.setPubDate(news.get(i).getPubDate());
                newsRepo.save(DBdata);
                System.out.println("news data 갱신 완료");
            }
        }

        return searchList.getBody();
    }
}
