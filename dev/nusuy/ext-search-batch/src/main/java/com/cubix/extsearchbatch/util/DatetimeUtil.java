package com.cubix.extsearchbatch.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DatetimeUtil {
    public static LocalDateTime valueOf(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        return LocalDateTime.parse(dateStr, formatter);
    }
}
