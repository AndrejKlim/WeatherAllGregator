package com.weatherallgregator.service;

import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import com.weatherallgregator.jpa.repo.ForecastLocationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForecastLocationService {
    
    private final ForecastLocationRepo repo;

    public ForecastLocationEntity saveLocation(float lat, float lon) {
        Optional<ForecastLocationEntity> locationEntity = repo.findByLatAndLon(lat, lon);

        if (locationEntity.isPresent()){
            return locationEntity.get();
        }else {
            ForecastLocationEntity newLocationEntity = new ForecastLocationEntity(lat, lon);
            return repo.save(newLocationEntity);
        }
    }
}
