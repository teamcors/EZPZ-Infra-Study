package com.cubix.extsearchbatch.util.data;

import com.cubix.extsearchbatch.dto.NaverDataResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsDataReader {
    @Value("${OPEN_API_BASE_URL}")
    private String BASE_URL;
    @Value("${OPEN_API_CLIENT_ID}")
    private String CLIENT_ID;
    @Value("${OPEN_API_CLIENT_SECRET}")
    private String CLIENT_SECRET;

    // Header setting
    public HttpHeaders setHeaders() {
        // Headers
        String CLIENT_ID_HEADER = "X-Naver-Client-Id";
        String CLIENT_SECRET_HEADER = "X-Naver-Client-Secret";

        HttpHeaders headers = new HttpHeaders();
        headers.add(CLIENT_ID_HEADER, CLIENT_ID);
        headers.add(CLIENT_SECRET_HEADER, CLIENT_SECRET);

        return headers;
    }

    // Url setting
    public String setUrl(int display, int start) {
        return BASE_URL +
                "?query=" + "주식" +
                "&display=" + display +
                "&start=" + start +
                "&sort=" + "sim";
    }

    // Naver API로 데이터 요청
    public NaverDataResponseDto get(int display, int start) throws Exception {
        String url = setUrl(display, start);
        HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(setHeaders());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverDataResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, httpEntity, NaverDataResponseDto.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Data request failed.");
        }

        return response.getBody();
    }
}
