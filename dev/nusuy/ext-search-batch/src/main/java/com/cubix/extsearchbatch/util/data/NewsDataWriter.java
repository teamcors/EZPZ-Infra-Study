package com.cubix.extsearchbatch.util.data;

import com.cubix.extsearchbatch.dto.NaverNewsItemDto;
import com.cubix.extsearchbatch.dto.NaverRawNewsItemDto;
import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.entity.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsDataWriter {
    private final DataValidator dataValidator;
    private final NewsRepository newsRepository;

    @Transactional
    public void write(boolean isEmptyDB, ArrayList<NaverRawNewsItemDto> items) {
        ArrayList<NewsEntity> validatedItems = new ArrayList<>();

        // Validate data
        if (!isEmptyDB) {
            validatedItems = dataValidator.getValidData(items);
        } else {
            for (NaverRawNewsItemDto rawNewsItemDto : items) {
                validatedItems.add(new NewsEntity(new NaverNewsItemDto(rawNewsItemDto)));
            }
        }

        // Save data
        newsRepository.saveAll(validatedItems);
    }
}
