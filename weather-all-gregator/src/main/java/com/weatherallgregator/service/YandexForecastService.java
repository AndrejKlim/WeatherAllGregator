package com.weatherallgregator.service;

import com.weatherallgregator.client.YandexApiClient;
import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.dto.User;
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

import static com.weatherallgregator.mapper.ForecastLocalizationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.YandexForecastMapper.readForecast;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexForecastService {

    private static final String YANDEX_API_FORECAST_NAME = "yandex";
    public static final Sort SORT_DESC_BY_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final YandexForecastRepo repo;
    private final YandexApiClient apiClient;
    private final ApiCallCounterService apiCallCounterService;

    public YandexForecast getForecast(final User user) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<YandexForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocation(mapToForecastLocationEntity(forecastLocation), SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get())) {
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return readForecast(lastForecastByLocation.get());
        }
        if (!apiCallCounterService.canApiCallBePerformed(YANDEX_API_FORECAST_NAME)) {
            log.info("Api call limit reached");
            return null; // TODO exception can be thrown or empty Optional
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        String jsonResponse = apiClient.getForecast(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());
        apiCallCounterService.incrementApiCallCounter(YANDEX_API_FORECAST_NAME);
        YandexForecastEntity entity = new YandexForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                jsonResponse,
                mapToForecastLocationEntity(forecastLocation));
        repo.save(entity);

        return readForecast(entity);
    }

    private boolean isExpired(YandexForecastEntity forecast) {
        var todayMorning = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
        long todayMorningSeconds = todayMorning.toEpochSecond(ZoneOffset.UTC);

        return forecast.getCreatedAt() < todayMorningSeconds;
    }
}
