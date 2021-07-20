package com.weatherallgregator.mapper;

import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.jpa.entity.ForecastLocationEntity;

public class ForecastLocationMapper {

    private ForecastLocationMapper(){

    }

    public static ForecastLocation mapToForecastLocation(final ForecastLocationEntity entity){
        ForecastLocation forecastLocation = new ForecastLocation();
        forecastLocation.setId(entity.getId());
        forecastLocation.setLat(entity.getLat());
        forecastLocation.setLon(entity.getLon());

        return forecastLocation;
    }

    public static ForecastLocationEntity mapToForecastLocationEntity(final ForecastLocation dto){
        ForecastLocationEntity locationEntity = new ForecastLocationEntity();
        locationEntity.setId(dto.getId());
        locationEntity.setLat(dto.getLat());
        locationEntity.setLon(dto.getLon());

        return locationEntity;
    }
}
