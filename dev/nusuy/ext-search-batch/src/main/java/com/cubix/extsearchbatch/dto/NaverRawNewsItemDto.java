package com.cubix.extsearchbatch.dto;

import com.cubix.extsearchbatch.entity.NewsEntity;
import com.cubix.extsearchbatch.util.DatetimeUtil;
import com.cubix.extsearchbatch.util.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NaverRawNewsItemDto {
    private String title;
    private String link;
    private String description;
    private String pubDate;

    public NewsEntity toEntity() {
        return NewsEntity.builder()
                .title(StringUtil.of(this.title))
                .url(this.link)
                .description(StringUtil.of(this.description))
                .publishedAt(DatetimeUtil.valueOf(this.pubDate))
                .build();
    }
}
