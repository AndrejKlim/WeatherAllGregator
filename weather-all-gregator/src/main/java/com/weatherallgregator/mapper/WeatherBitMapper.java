package com.weatherallgregator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.dto.weatherbit.WeatherBitWeather;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherBitMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private WeatherBitMapper(){

    }

    public static WeatherBitWeather readForecast(final ForecastEntity forecastEntity){
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            return objectMapper.readValue(forecastEntity.getForecast(), WeatherBitWeather.class);
        } catch (JsonProcessingException e) {
            log.error("Error during parsing Weatherbit weather. Message - {}", e.getMessage());
        }
        return null;
    }
}
