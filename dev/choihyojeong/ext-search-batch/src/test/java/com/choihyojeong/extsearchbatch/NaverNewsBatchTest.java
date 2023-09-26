package com.choihyojeong.extsearchbatch;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
class NaverNewsBatchTest {



    @Autowired
    NaverNewsRepository NaverNewsRepoTest;
    @Autowired
    NaverNewsService NaverNewsService;
    
    @Value("${naver.client.id}")
    private String CLIENT_ID;

    @Value("${naver.client.secret}")
    private String CLIENT_SECRET;

    @Value("${naver.base.url}")
    private String BASE_URL;

    @Value("${naver.base.path}")
    private String PATH;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;


    private RestTemplate restTemplate = new RestTemplate();

    int success = 0;

    @DisplayName("BatchTest: 새로운 뉴스와 적재된 뉴스의 개수가 같다.")
    @Test
    public void getBatchNewsNumber() throws Exception {
        //given
        int saveNewsLen = 0; // DB에 저장된 뉴스 개수
        NaverNewsService.naverNewsApi();

        updateDate = LocalDateTime.now();
        String checkUpDate = updateDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentDate = LocalDateTime.parse(checkUpDate,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        //when
        // DB에 적재된 뉴스 개수를 불러올 방법을 생각해야함
        // 1. 최근 10개를 불러와 (id로)
        List<NaverNewsItem> findtop10 = NaverNewsRepoTest.findTop10ByOrderByIdDesc(); //id 내림차순으로 top 10개 가져옴
        List<String> latestTitle = findtop10.stream().map(NaverNewsItem::getTitle).collect(Collectors.toList()); //top 10개에서 title만 구함
        List<LocalDateTime> latestDate = findtop10.stream().map(NaverNewsItem::getUpdated_at).collect(Collectors.toList());

        // 2. 불러온 10개 중에 업데이트 된 시간이 현재시간인지
        for(int i=0; i<10; i++){
            if(latestDate.get(i).equals(currentDate) || latestDate.get(i).equals(currentDate.plusSeconds(1)) || latestDate.get(i).equals(currentDate.minusSeconds(1))){
                System.out.println("새로 적재된 뉴스입니다 : " +latestTitle.get(i));
                saveNewsLen = saveNewsLen +1;
            }
            else{
                System.out.println("새로 적재된 뉴스가 아닙니다 : "+latestTitle.get(i));
            }
        }
        System.out.println("적재된 뉴스 개수"+ saveNewsLen);

        //then
        // 3. 그 개수가 0에서 10사이인지
        Assertions.assertThat(saveNewsLen).isBetween(0,10);

    }

    @DisplayName("KeywordTest: 적재된 뉴스 title, description에 '주식'이 들어가있다")
    @Test
    public void checkKeyword() throws Exception {

        //given
        List<NaverNewsItem> findtop10 = NaverNewsRepoTest.findTop10ByOrderByIdDesc(); //id 내림차순으로 top 10개 가져옴

        //top 10개에서 title만 구함
        // 1. findtop10 list에서 gettitle로 title만 받아와 새로운 list로 저장
        List<String> latestTitle = findtop10.stream().map(NaverNewsItem::getTitle).collect(Collectors.toList());

        //when
        for (int i = 0; i < 10; i++) {
            // 1. 최신 10개의 뉴스에
            // 2.decription과 title에 '주식' 키워드가 있는지
            if(latestTitle != null) {
                NaverNewsItem savedNews = NaverNewsRepoTest.findByTitle((latestTitle.get(i)));
                if (savedNews.getTitle().contains("주식")) {
                    success = success +1;
                } else if (savedNews.getDescription().contains("주식")) {
                    success = success +1;
                }
            }
            else {
                System.out.println("적재된 뉴스 데이터가 없습니다");
            }
        }

        //then
        Assertions.assertThat(success).isEqualTo(10);
    }
    private MultiValueMap<String, String> httpConnect() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", CLIENT_ID);
        headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
        return headers;
    }
}