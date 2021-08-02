package com.weatherallgregator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledNotification {

    private String chatId;
    private String notificationTime;
    private User user;
    private String forecastType;
    private String sources; // format - SOURCE,SOURCE,SOURCE...
}
