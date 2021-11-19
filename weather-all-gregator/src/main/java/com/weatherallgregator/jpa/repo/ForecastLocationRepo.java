package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForecastLocationRepo extends JpaRepository<ForecastLocationEntity, Long> {

    Optional<ForecastLocationEntity> findByLatAndLon(Float lat, Float lon);
}
