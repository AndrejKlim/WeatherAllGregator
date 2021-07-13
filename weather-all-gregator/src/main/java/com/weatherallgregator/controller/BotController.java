package com.weatherallgregator.controller;

import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.mapper.YandexForecastMapper;
import com.weatherallgregator.service.YandexForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotController {

    private final YandexForecastService yandexForecastService;

    public String getFactForecast(){
        YandexForecast forecast = yandexForecastService.getForecast("53.6884", "23.8258");
        return YandexForecastMapper.mapToFactYandexModel(forecast).toRuStringResponse();
    }

    public String getForecast() {
        YandexForecast forecast = yandexForecastService.getForecast("53.6884", "23.8258");
        return YandexForecastMapper.mapToYandexModel(forecast).toRuStringResponse();
    }
}
