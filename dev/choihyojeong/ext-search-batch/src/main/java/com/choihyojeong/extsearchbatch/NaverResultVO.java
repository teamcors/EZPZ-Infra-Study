package com.choihyojeong.extsearchbatch;
import java.util.List;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NaverResultVO {
    private String lastBuildDate;
    private Long total;
    private Long start;
    private Long display;
    private List<NaverNewsItem> items;
}
