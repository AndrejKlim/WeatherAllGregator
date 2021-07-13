package com.weatherallgregator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.dto.Part;
import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.enums.Condition;
import com.weatherallgregator.enums.DayTime;
import com.weatherallgregator.jpa.entity.YandexForecastEntity;
import com.weatherallgregator.model.FactYandexModel;
import com.weatherallgregator.model.YandexForecastModel;
import com.weatherallgregator.model.YandexPartModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class YandexForecastMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private YandexForecastMapper(){

    }

    public static YandexForecast readForecast(final YandexForecastEntity entity){
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        try {
            return objectMapper.readValue(entity.getForecast(), YandexForecast.class);
        } catch (JsonProcessingException e) {
            log.error("Error during parsing Yandex forecast. Message - {}", e.getMessage());
        }
        return null;
    }

    public static FactYandexModel mapToFactYandexModel(final YandexForecast forecast){
        var model = new FactYandexModel();
        model.setTemp(forecast.getFact().getTemp());
        model.setCondition(Condition.valueOfCondition(forecast.getFact().getCondition()));
        model.setWindSpeed(forecast.getFact().getWindSpeed());
        model.setPressureMm(forecast.getFact().getPressureMm());
        model.setHumidity(forecast.getFact().getHumidity());

        return model;
    }

    public static YandexForecastModel mapToYandexModel(final YandexForecast forecast){
        var model = new YandexForecastModel();
        model.setParts(forecast.getForecast().getParts()
                .stream().filter(Objects::nonNull).map(YandexForecastMapper::mapToYandexPartModel).collect(Collectors.toList()));
        return model;
    }

    private static YandexPartModel mapToYandexPartModel(final Part part) {
        var model = new YandexPartModel();
        model.setDayTime(DayTime.valueOfDayTime(part.getPartName()));
        model.setAverageTemp(part.getTempAvg());
        model.setCondition(Condition.valueOfCondition(part.getCondition()));
        model.setWindSpeed(part.getWindSpeed());
        model.setPressureMm(part.getPressureMm());
        model.setHumidity(part.getHumidity());
        model.setPrecipitationMm(part.getPrecMm());
        model.setPrecipitationProbability(part.getPrecProb());

        return model;
    }
}
