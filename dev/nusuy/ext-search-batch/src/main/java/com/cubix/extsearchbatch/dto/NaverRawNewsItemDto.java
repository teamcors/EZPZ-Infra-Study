package com.cubix.extsearchbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NaverRawNewsItemDto {
    private String title;
    private String link;
    private String description;
    private String pubDate;
}
