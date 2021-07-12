package com.weatherallgregator.service;

import com.weatherallgregator.client.YandexApiClient;
import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.jpa.entity.YandexForecastEntity;
import com.weatherallgregator.jpa.repo.YandexForecastRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.weatherallgregator.mapper.YandexForecastMapper.readForecast;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexForecastService {

    private static final String YANDEX_API_FORECAST_NAME = "yandex";

    private final YandexForecastRepo repo;
    private final YandexApiClient apiClient;
    private final ApiCallCounterService apiCallCounterService;

    public YandexForecast getForecast(final String latitude, final String longitude){
        Optional<YandexForecastEntity> lastForecast = repo.findFirstBy(Sort.by(Sort.Direction.DESC, "createdAt"));

        if (lastForecast.isPresent() && !isExpired(lastForecast.get())){
            return readForecast(lastForecast.get());
        }
        if (!apiCallCounterService.canApiCallBePerformed(YANDEX_API_FORECAST_NAME)){
            return null; // TODO exception can be thrown or empty Optional
        }

        String jsonResponse = apiClient.getForecast(latitude, longitude);
        apiCallCounterService.incrementApiCallCounter(YANDEX_API_FORECAST_NAME);
        YandexForecastEntity entity = new YandexForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), jsonResponse);
        repo.save(entity);

        return readForecast(entity);
    }

    private boolean isExpired(YandexForecastEntity forecast) {
        var todayMorning = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
        long todayMorningSeconds = todayMorning.toEpochSecond(ZoneOffset.UTC);

        return forecast.getCreatedAt() < todayMorningSeconds;
    }
}
