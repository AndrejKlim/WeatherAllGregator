package com.weatherallgregator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.client.YandexApiClient;
import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.jpa.repo.YandexForecastRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexForecastService {

    private final YandexForecastRepo repo;
    private final YandexApiClient apiClient;
    private final ApiCallCounterService apiCallCounterService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public YandexForecast getForecast(final String latitude, final String longitude){
        return null; // TODO implement
    }
}
