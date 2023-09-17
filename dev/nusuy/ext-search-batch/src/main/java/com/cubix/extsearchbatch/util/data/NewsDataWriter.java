package com.cubix.extsearchbatch.util.data;

import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.entity.NewsRepository;
import com.cubix.extsearchbatch.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsDataWriter {
    private final NewsRepository newsRepository;

    // 중복 검사
    public NewsEntity getDuplicateData(NaverRawNewsItemDto naverRawNewsItemDto) {
        String title = StringUtil.of(naverRawNewsItemDto.getTitle());

        return newsRepository.findTop1ByTitle(title);
    }

    @Transactional
    public void writeWithValidation(ArrayList<NaverRawNewsItemDto> items) {
        ArrayList<NewsEntity> validatedItems = new ArrayList<>();

        for (NaverRawNewsItemDto rawNewsItemDto : items) {
            // 중복 데이터 검사 (기준: title)
            NewsEntity duplicateEntity = getDuplicateData(rawNewsItemDto);
            if (duplicateEntity != null) {
                // 중복 데이터 존재 시 업데이트
                duplicateEntity.update(rawNewsItemDto.toEntity());
                continue;
            }

            validatedItems.add(rawNewsItemDto.toEntity());
        }

        // Save data
        newsRepository.saveAll(validatedItems);
    }

    public void writeWithoutValidation(ArrayList<NaverRawNewsItemDto> items) {
        ArrayList<NewsEntity> resultList = new ArrayList<>();

        for (NaverRawNewsItemDto rawNewsItemDto : items) {
            resultList.add(rawNewsItemDto.toEntity());
        }

        // Save data
        newsRepository.saveAll(resultList);
    }
}
