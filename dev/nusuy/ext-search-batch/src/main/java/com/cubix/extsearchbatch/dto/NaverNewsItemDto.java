package com.cubix.extsearchbatch.dto;

import com.cubix.extsearchbatch.util.DatetimeUtil;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NaverNewsItemDto {
    private String title;
    private String link;
    private String description;
    private LocalDateTime pubDate;

    public NaverNewsItemDto(NaverRawNewsItemDto dto) {
        this.title = dto.getTitle().replaceAll("<b>|</b>", "");
        this.link = dto.getLink();
        this.description = dto.getDescription().replaceAll("<b>|</b>", "");
        this.pubDate = DatetimeUtil.valueOf(dto.getPubDate());
    }
}
