package com.weatherallgregator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.dto.openweather.OpenWeatherForecast;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenWeatherMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private OpenWeatherMapper(){

    }

    public static OpenWeatherForecast readForecast(final ForecastEntity forecastEntity){
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            return objectMapper.readValue(forecastEntity.getForecast(), OpenWeatherForecast.class);
        } catch (JsonProcessingException e) {
            log.error("Error during parsing OpenWeather forecast. Message - {}", e.getMessage());
        }
        return null;
    }
}
