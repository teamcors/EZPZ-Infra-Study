package com.choihyojeong.extsearchbatch;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.assertj.core.api.Assertions;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
@AutoConfigureMockMvc

class NaverNewsBatchTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    NaverNewsRepository NaverNewsRepoTest;
    
    @Value("${naver.client.id}")
    private String CLIENT_ID;

    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;

    @Value("${naver.base.url}")
    private String BASE_URL;

    @Value("${naver.base.path}")
    private String PATH;

    private RestTemplate restTemplate = new RestTemplate();

    List<String> newNewsTitle = new ArrayList<>(); //새로 적재한 뉴스 title

    @DisplayName("BatchTest: 새로운 뉴스와 적재된 뉴스의 개수가 같다.")
    @Test
    public void getBatchNewsNumber() throws Exception {

        //given
        // Naver 검색 API 호출
        System.out.println("given-Naver 검색 API 호출 시작");
        URI BUrl = UriComponentsBuilder
                .fromUriString(BASE_URL)
                .path(PATH)
                .queryParam("query", "주식")
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "date") //issue #2
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
        List<NaverNewsItem> saveItem = new ArrayList<>(); //한번에 저장하기 위한 배열

        int newNewsLen = 0; //새로운 뉴스 개수
        int saveNewsLen = 0; // DB에 저장된 뉴스 개수

        //When
        //반환 결과 DB에 저장
        for (NaverNewsItem news : newsItem) {

            //기존에 있는 데이터 = findNews
            //System.out.println("1"+news);
            NaverNewsItem findNews = NaverNewsRepoTest.findByTitle((news.getTitle()));


            String newNews = news.getLink();


            if (findNews == null) {
                saveItem.add(news);
                System.out.println("뉴스 데이터 적재 완료");
                newNewsTitle.add(news.getTitle()); //새로운 뉴스 제목만 저장
                newNewsLen = newNewsLen + 1;

            } else {
                String oldNews = findNews.getLink();
                if (!oldNews.equals(newNews)) {
                    findNews.setDescription(news.getDescription());
                    findNews.setLink(news.getLink());
                    findNews.setOriginallink(news.getOriginallink());
                    findNews.setPubDate(news.getPubDate());


                    saveItem.add(findNews);
                    newNewsTitle.add(findNews.getTitle()); //새로운 뉴스 제목만 저장
                    newNewsLen = newNewsLen + 1;

                    System.out.println("뉴스 데이터 업데이트 완료 :  "+findNews.getTitle());
                }
                else {
                    System.out.println("최신 뉴스입니다 :  " +findNews.getTitle());
                }
            }
            NaverNewsRepoTest.saveAll(saveItem);
        }

        //Then
        //반환 결과 중 새로운 뉴스 개수와 DB에 적재된 뉴스 개수가 같은지 (0~10)
        //NaverNewsItem chekUpdate = NaverNewsRepoTest.findByTitle((news.getTitle()));

        // DB에 적재된 뉴스 개수를 불러올 방법을 생각해야함
        // 1. 최근 10개를 불러와 (id로)
        List<NaverNewsItem> findtop10 = NaverNewsRepoTest.findTop10ByOrderByIdDesc(); //id 내림차순으로 top 10개 가져옴
        List<String> latestTitle = findtop10.stream().map(NaverNewsItem::getTitle).collect(Collectors.toList()); //top 10개에서 title만 구함

        // 2. 불러온 10개 중에 뉴스 제목이 같은게 있는지

        if(newNewsTitle != null) //새로 저장된 뉴스들이 있을 떄
        {
            for(int i=1; i<=newNewsTitle.size() ; i++){ // 새로 저장된 뉴스들 개수만큼 반복해서
                if(latestTitle.contains(newNewsTitle.get(i-1))){ //제대로 저장됐는지 확인
                    System.out.println("새로 적재된 뉴스입니다 :  " +newNewsTitle.get(i-1));
                    saveNewsLen = saveNewsLen +1;
                }
            }
        }
        else { //새로 저장된 뉴스들이 없을 때
            System.out.println("새로 적재된 뉴스가 없습니다.");
        }

        System.out.println("새로운 뉴스 개수" + newNewsLen);
        System.out.println("적재된 뉴스 개수"+ saveNewsLen);

        // 3. 그 개수랑 새로운 뉴스 개수랑 같은지 확인하기
        Assertions.assertThat(newNewsLen).isEqualTo(saveNewsLen);

    }
    private MultiValueMap<String, String> httpConnect() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", CLIENT_ID);
        headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
        return headers;
    }
}