package com.cubix.extsearchbatch.util.data;

import com.cubix.extsearchbatch.dto.NaverNewsItemDto;
import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.entity.NewsRepository;
import com.cubix.extsearchbatch.util.DatetimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataValidator {
    private final NewsRepository newsRepository;

    // 중복 검사
    public NewsEntity getDuplicateData(NaverRawNewsItemDto naverRawNewsItemDto) {
        String title = naverRawNewsItemDto.getTitle().replaceAll("<b>|</b>", "");
        String link = naverRawNewsItemDto.getLink();
        String description = naverRawNewsItemDto.getDescription().replaceAll("<b>|</b>", "");
        LocalDateTime pubDate = DatetimeUtil.valueOf(naverRawNewsItemDto.getPubDate());

        return newsRepository.findByTitleAndUrlAndPublishedAtWithJPQL(title, link, description, pubDate);
    }

    // 저장 가능한 데이터들만 반환
    public ArrayList<NewsEntity> getValidData(ArrayList<NaverRawNewsItemDto> items) {
        ArrayList<NewsEntity> result = new ArrayList<>();

        for (NaverRawNewsItemDto itemDto : items) {
            // 중복 데이터 검사
            NewsEntity duplicateEntity = getDuplicateData(itemDto);
            if (duplicateEntity != null) {
                // url이 같으면 내용 업데이트
                if (itemDto.getLink().equals(duplicateEntity.getUrl()))
                    duplicateEntity.update(new NaverNewsItemDto(itemDto));

                continue;
            }

            // 새로 저장할 데이터 리스트에 저장
            result.add(new NewsEntity(new NaverNewsItemDto(itemDto)));
        }

        return result;
    }
}
