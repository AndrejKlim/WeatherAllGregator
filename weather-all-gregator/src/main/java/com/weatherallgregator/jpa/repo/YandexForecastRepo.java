package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import com.weatherallgregator.jpa.entity.YandexForecastEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YandexForecastRepo extends JpaRepository<YandexForecastEntity, Long> {

    Optional<YandexForecastEntity> findFirstByForecastLocation(ForecastLocationEntity entity, Sort sort);
}
