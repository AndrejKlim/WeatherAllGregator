package com.weatherallgregator.service;

import com.weatherallgregator.jpa.entity.ApiCallCounter;
import com.weatherallgregator.jpa.repo.ApiCallCounterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiCallCounterService {

    private static final int YANDEX_API_CALL_DAY_LIMIT = 50;
    private static final String YANDEX_FORECAST_NAME = "yandex";

    private final ApiCallCounterRepo repo;

    public boolean canYandexApiCallBePerformed(){
        Optional<ApiCallCounter> apiCallCounter = repo.findByApi(YANDEX_FORECAST_NAME);
        return apiCallCounter.isPresent() && apiCallCounter.get().getCounter() < YANDEX_API_CALL_DAY_LIMIT;
    }
}
