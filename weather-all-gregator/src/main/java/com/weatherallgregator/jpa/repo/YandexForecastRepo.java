package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.YandexForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YandexForecastRepo extends JpaRepository<YandexForecastEntity, Long> {

}
