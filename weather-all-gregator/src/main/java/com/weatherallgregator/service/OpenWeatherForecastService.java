package com.weatherallgregator.service;

import com.weatherallgregator.client.OpenWeatherApiClient;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.dto.openweather.OpenWeatherForecast;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.weatherallgregator.enums.ForecastSource.OPEN_WEATHER;
import static com.weatherallgregator.enums.ForecastType.FORECAST;
import static com.weatherallgregator.enums.ForecastType.WEATHER;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.OpenWeatherMapper.readForecast;

@Component
@Slf4j
public class OpenWeatherForecastService extends ForecastService{

    public static final String NO_INFO = "No info";
    private final OpenWeatherApiClient apiClient;

    public OpenWeatherForecastService(final ForecastRepo repo,
                                      final ApiCallCounterService apiCallCounterService,
                                      final OpenWeatherApiClient apiClient) {
        super(repo, apiCallCounterService);
        this.apiClient = apiClient;
    }

    @Override
    public WeatherInfo getWeather(final User user) {
        return getOpenWeatherForecast(user, WEATHER)
                .map(f -> (WeatherInfo) f)
                .orElse(() -> NO_INFO);
    }

    @Override
    public ForecastInfo getForecast(final User user) {
        return getOpenWeatherForecast(user, FORECAST)
                .map(f -> (ForecastInfo) f)
                .orElse(() -> List.of(NO_INFO));
    }

    @Override
    public ForecastSource getSource() {
        return OPEN_WEATHER;
    }

    public Optional<OpenWeatherForecast> getOpenWeatherForecast(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSource(mapToForecastLocationEntity(forecastLocation),
                        OPEN_WEATHER.name(),
                        SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
        }
        if (!apiCallCounterService.canApiCallBePerformed(OPEN_WEATHER)) {
            log.info("Api call limit reached");
            return Optional.empty();
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        String jsonResponse = apiClient.getCurrentWeather(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());
        apiCallCounterService.incrementApiCallCounter(OPEN_WEATHER);
        ForecastEntity entity = new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                jsonResponse,
                OPEN_WEATHER.name(),
                mapToForecastLocationEntity(forecastLocation));
        repo.save(entity);

        return Optional.ofNullable(readForecast(entity));
    }
}
