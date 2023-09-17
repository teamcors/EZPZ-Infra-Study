package com.cubix.extsearchbatch.service;

import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.entity.NewsRepository;
import com.cubix.extsearchbatch.util.data.NewsDataReader;
import com.cubix.extsearchbatch.util.data.NewsDataWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataUpdateService {
    private final NewsRepository newsRepository;
    private final NewsDataReader newsDataReader;
    private final NewsDataWriter newsDataWriter;

    @PostConstruct
    public void onStartup() throws Exception {
        updateNewsData();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void updateNewsData() throws Exception {
        log.info("Naver news data request started. --" + LocalDateTime.now());

        // Get total data count
        final int DISPLAY_DEF = 100;
        boolean isEmptyDB = newsRepository.count() == 0;
        int totalCount = newsDataReader.get(1, 1).getTotal();
        int batchCount = (int) Math.ceil((double) totalCount / DISPLAY_DEF);
        int displayLast = totalCount % DISPLAY_DEF;

        try {
            for (int i = 1; i <= batchCount; i++) {
                int start = (i - 1) * DISPLAY_DEF + 1;
                int display = i == batchCount ? displayLast : DISPLAY_DEF;

                // 요청 가능 범위 밖일 경우
                if (start > 1000) {
                    log.info("Naver news data request completed successfully. --" + LocalDateTime.now());

                    return;
                }

                // Get data
                ArrayList<NaverRawNewsItemDto> items = newsDataReader.get(display, start).getItems();

                // Write data
                newsDataWriter.write(isEmptyDB, items);
            }

            log.info("Naver news data request completed successfully. --" + LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
