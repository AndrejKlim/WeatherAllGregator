package com.weatherallgregator.service;

import com.weatherallgregator.client.YandexApiClient;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.dto.yandex.YandexForecast;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import com.weatherallgregator.mapper.YandexForecastMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.weatherallgregator.enums.ForecastSource.YANDEX;
import static com.weatherallgregator.enums.ForecastType.FORECAST;
import static com.weatherallgregator.enums.ForecastType.WEATHER;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.YandexForecastMapper.readForecast;

@Component
@Slf4j
public class YandexForecastService extends ForecastService{

    public static final String NO_INFO = "No info";
    private final YandexApiClient apiClient;

    public YandexForecastService(final ForecastRepo repo,
                                 final ApiCallCounterService apiCallCounterService,
                                 final YandexApiClient apiClient) {
        super(repo, apiCallCounterService);
        this.apiClient = apiClient;
    }

    @Override
    public WeatherInfo getWeather(final User user) {
        return getYandexForecast(user, WEATHER)
                .map(WeatherInfo.class::cast)
                .orElse(() -> NO_INFO);
    }

    @Override
    public ForecastInfo getForecast(final User user) {
        return getYandexForecast(user, FORECAST)
                .map(ForecastInfo.class::cast)
                .orElse(() -> List.of(NO_INFO));
    }

    @Override
    public ForecastSource getSource() {
        return YANDEX;
    }

    public Optional<YandexForecast> getYandexForecast(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSource(mapToForecastLocationEntity(forecastLocation),
                        YANDEX.name(),
                        SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)) {
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
        }
        if (!apiCallCounterService.canApiCallBePerformed(YANDEX)) {
            log.info("Api call limit reached");
            return Optional.empty();
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        Optional<String> jsonResponse = apiClient.getForecast(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());
        jsonResponse.ifPresent(json -> apiCallCounterService.incrementApiCallCounter(YANDEX));
        Optional<ForecastEntity> entity = jsonResponse.map(json -> new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                json,
                YANDEX.name(),
                mapToForecastLocationEntity(forecastLocation)));
        entity.ifPresent(repo::save);

        return entity.map(YandexForecastMapper::readForecast);
    }
}
