package com.weatherallgregator.service;

import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public abstract class ForecastService {

    public static final Sort SORT_DESC_BY_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

    protected final ForecastRepo repo;
    protected final ApiCallCounterService apiCallCounterService;

    public ForecastService(final ForecastRepo repo, final ApiCallCounterService apiCallCounterService) {
        this.repo = repo;
        this.apiCallCounterService = apiCallCounterService;
    }

    public abstract WeatherInfo getWeather(final User user);

    public abstract ForecastInfo getForecast(final User user);

    public abstract ForecastSource getSource();

    protected boolean isExpired(final ForecastEntity forecast) {
        // FIXME: 2.08.21 change checking for expiring, for different types of forecasts and api
        var todayMorning = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
        long todayMorningSeconds = todayMorning.toEpochSecond(ZoneOffset.UTC);

        return forecast.getCreatedAt() < todayMorningSeconds;
    }
}
