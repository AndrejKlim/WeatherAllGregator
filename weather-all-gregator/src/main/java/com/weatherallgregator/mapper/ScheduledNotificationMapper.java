package com.weatherallgregator.mapper;

import com.weatherallgregator.dto.ScheduledNotification;
import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;

public class ScheduledNotificationMapper {

    private ScheduledNotificationMapper(){

    }

    public static ScheduledNotification mapToScheduledNotification(final ScheduledNotificationEntity entity){
        var dto = new ScheduledNotification();
        dto.setChatId(entity.getChatId());
        dto.setNotificationTime(entity.getNotificationTime());
        dto.setUser(UserMapper.mapToUser(entity.getUser()));
        dto.setForecastType(entity.getForecastType());
        dto.setSources(entity.getSources());

        return dto;
    }

    public static ScheduledNotificationEntity mapToScheduledNotificationEntity(final ScheduledNotification dto){
        var entity = new ScheduledNotificationEntity();
        entity.setChatId(dto.getChatId());
        entity.setNotificationTime(dto.getNotificationTime());
        entity.setUser(UserMapper.mapToUserEntity(dto.getUser()));
        entity.setForecastType(dto.getForecastType());
        entity.setSources(dto.getSources());

        return entity;
    }
}
