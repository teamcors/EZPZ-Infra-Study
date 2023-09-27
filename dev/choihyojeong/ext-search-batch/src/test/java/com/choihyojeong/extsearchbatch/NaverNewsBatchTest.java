package com.choihyojeong.extsearchbatch;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;;
import org.springframework.transaction.annotation.Transactional;
import org.assertj.core.api.Assertions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class NaverNewsBatchTest {

    @Autowired
    NaverNewsRepository NaverNewsRepoTest;
    @Autowired
    NaverNewsService NaverNewsService;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    int success = 0;
    
    @Test
    @DisplayName("BatchTest: 새로운 뉴스와 적재된 뉴스의 개수가 같다.")
    public void getBatchNewsNumber() throws Exception {
        //given
        int saveNewsLen = 0; // DB에 저장된 뉴스 개수
        NaverNewsService.naverNewsApi();
        updateDate = LocalDateTime.now();
        String checkUpDate = updateDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentDate = LocalDateTime.parse(checkUpDate,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //when
        // 1. DB에 적재된 최근 10개를 id로 불러옴
        List<NaverNewsItem> findtop10 = NaverNewsRepoTest.findTop10ByOrderByIdDesc(); //id 내림차순으로 top 10개 가져옴
        List<String> latestTitle = findtop10.stream().map(NaverNewsItem::getTitle).collect(Collectors.toList()); //top 10개에서 title만 구함
        List<LocalDateTime> latestDate = findtop10.stream().map(NaverNewsItem::getUpdated_at).collect(Collectors.toList());

        // 2. 불러온 10개 중에 업데이트 된 시간이 현재시간인지 (현재시간 오차범쉬 +- 1초)
        for(int i=0; i<10; i++){
            if(latestDate.get(i).equals(currentDate) || latestDate.get(i).equals(currentDate.plusSeconds(1)) || latestDate.get(i).equals(currentDate.minusSeconds(1))){
                log.info("새로 적재된 뉴스입니다 : " +latestTitle.get(i));
                saveNewsLen = saveNewsLen +1;
            }
            else{
                log.info("새로 적재된 뉴스가 아닙니다 : "+latestTitle.get(i));
            }
        }
        log.info("적재된 뉴스 개수"+ saveNewsLen);

        //then
        // 3. 그 개수가 0에서 10사이인지
        Assertions.assertThat(saveNewsLen).as("새로 적재된 뉴스의 개수가 0~10범위에 들지 않음").isBetween(0,10);
    }

    @Test
    @DisplayName("KeywordTest: 적재된 뉴스 title, description에 '주식'이 들어가있다")
    public void checkKeyword() throws Exception {

        //given
        NaverNewsService.naverNewsApi();
        List<NaverNewsItem> findtop10 = NaverNewsRepoTest.findTop10ByOrderByIdDesc(); //id 내림차순으로 top 10개 가져옴

        // 1. findtop10 list에서 gettitle로 title만 받아와 새로운 list로 저장
        List<String> latestTitle = findtop10.stream().map(NaverNewsItem::getTitle).collect(Collectors.toList());

        //when
        for (int i = 0; i < 10; i++) {
            // decription과 title에 '주식' 키워드가 있는지
            NaverNewsItem savedNews = NaverNewsRepoTest.findByTitle((latestTitle.get(i)));
            if (savedNews.getTitle().contains("주식")) {
                success = success +1;
            } else if (savedNews.getDescription().contains("주식")) {
                success = success +1;
            }
        }

        //then
        Assertions.assertThat(success).as("주식키워드가 들어간 뉴스가 10개가 아님").isEqualTo(10);
    }
}