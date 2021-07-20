package com.weatherallgregator.service;

import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;
import com.weatherallgregator.jpa.repo.ScheduledNotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final ScheduledNotificationRepo scheduledNotificationRepo;

    public ScheduledNotificationEntity save(final ScheduledNotificationEntity entity){
        return scheduledNotificationRepo.save(entity);
    }

    public void delete(final String chatId){
        scheduledNotificationRepo.deleteById(chatId);
    }
}
