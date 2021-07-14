package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledNotificationRepo extends JpaRepository<ScheduledNotificationEntity, String> {
}
