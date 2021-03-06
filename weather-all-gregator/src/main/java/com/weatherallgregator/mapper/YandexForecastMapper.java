package com.weatherallgregator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.dto.yandex.YandexForecast;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YandexForecastMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private YandexForecastMapper(){

    }

    public static YandexForecast readForecast(final ForecastEntity entity){
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            return objectMapper.readValue(entity.getForecast(), YandexForecast.class);
        } catch (JsonProcessingException e) {
            log.error("Error during parsing Yandex forecast. Message - {}", e.getMessage());
        }
        return null;
    }
}
