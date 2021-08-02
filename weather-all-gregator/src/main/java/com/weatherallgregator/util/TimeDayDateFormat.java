package com.weatherallgregator.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

public class TimeDayDateFormat {


    public static final DateTimeFormatter HOUR_DAY;

    static {
        Map<Long, String> dow = new HashMap<>();
        dow.put(1L,"Понедельние");
        dow.put(2L,"Вторник");
        dow.put(3L,"Среда");
        dow.put(4L,"Четверг");
        dow.put(5L,"Пятница");
        dow.put(6L,"Суббота");
        dow.put(7L,"Воскресенье");

        HOUR_DAY = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(":")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(" ")
                .appendText(ChronoField.DAY_OF_WEEK, dow)
                .appendLiteral(", ")
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .appendLiteral("-")
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral("-")
                .appendValue(ChronoField.YEAR, 4)
                .toFormatter();
    }
}
