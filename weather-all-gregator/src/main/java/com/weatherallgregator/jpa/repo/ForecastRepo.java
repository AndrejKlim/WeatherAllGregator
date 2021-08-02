package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForecastRepo extends JpaRepository<ForecastEntity, Long> {

    Optional<ForecastEntity> findFirstByForecastLocationAndSource(ForecastLocationEntity entity, String source, Sort sort);
}
