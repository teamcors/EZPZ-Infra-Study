package com.choihyojeong.extsearchbatch;
import static org.junit.jupiter.api.Assertions.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
class NaverNewsKeywordTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    NaverNewsRepository NaverNewsRepoTest;

    private RestTemplate restTemplate = new RestTemplate();

    //List<String> newNewsTitle = new ArrayList<>(); //새로 적재한 뉴스 title

    int success = 0;
    int failed =0;

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
            if(latestTitle != null)
            {
                NaverNewsItem savedNews = NaverNewsRepoTest.findByTitle((latestTitle.get(i)));
                if (savedNews.getTitle().contains("주식"))
                {
                    success = success +1;
                } else if (savedNews.getDescription().contains("주식"))
                {
                    success = success +1;
                }
            }
            else
            {
                System.out.println("적재된 뉴스 데이터가 없습니다");
            }
        }

        //then
        Assertions.assertThat(success).isEqualTo(10);
    }
}