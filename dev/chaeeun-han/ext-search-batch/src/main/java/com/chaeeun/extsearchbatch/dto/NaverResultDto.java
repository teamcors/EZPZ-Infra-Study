package com.chaeeun.extsearchbatch.dto;

import java.util.List;

import com.chaeeun.extsearchbatch.domain.NaverNewsItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NaverResultDto {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NaverNewsItem> items;
}
