package com.openai36.aggregation.common;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static DateTimeFormatter fm1 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * @return yyyyMMddHHmmss
     */
    public static String currentDateTime() {
        return java.time.LocalDateTime.now().format(fm1);
    }
}
