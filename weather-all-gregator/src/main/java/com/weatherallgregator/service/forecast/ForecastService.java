package com.weatherallgregator.service.forecast;

import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import com.weatherallgregator.service.ApiCallCounterService;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public abstract class ForecastService {

    public static final Sort SORT_DESC_BY_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

    protected final ForecastRepo repo;
    protected final ApiCallCounterService apiCallCounterService;

    protected ForecastService(final ForecastRepo repo, final ApiCallCounterService apiCallCounterService) {
        this.repo = repo;
        this.apiCallCounterService = apiCallCounterService;
    }

    public abstract WeatherInfo getWeather(final User user);

    public abstract ForecastInfo getForecast(final User user);

    public abstract ForecastSource getSource();

    protected boolean isExpired(final ForecastEntity forecast, final ForecastType forecastType) {
        long expiredTime = 0;
        if (forecastType == ForecastType.WEATHER) expiredTime = 7200; // 2 hours
        if (forecastType == ForecastType.FORECAST) expiredTime = 28800; // 8 hours

        final var forecastCreatedAt = forecast.getCreatedAt();
        final var now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        return now - forecastCreatedAt > expiredTime;
    }
}
