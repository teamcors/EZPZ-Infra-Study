package com.cubix.extsearchbatch.util;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {
    public static String of(String str) {
        return str.replaceAll("<b>|</b>", "");
    }
}
