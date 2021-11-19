package com.weatherallgregator.service;

import com.weatherallgregator.client.WeatherBitApiClient;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.dto.weatherbit.WeatherBitWeather;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import com.weatherallgregator.mapper.WeatherBitMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static com.weatherallgregator.enums.ForecastSource.WEATHERBIT;
import static com.weatherallgregator.enums.ForecastType.WEATHER;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.WeatherBitMapper.readForecast;

@Component
@Slf4j
public class WeatherBitService extends ForecastService{

    public static final String NO_INFO = "No info";
    private final WeatherBitApiClient apiClient;

    public WeatherBitService(final ForecastRepo repo,
                             final ApiCallCounterService apiCallCounterService,
                             final WeatherBitApiClient apiClient) {
        super(repo, apiCallCounterService);
        this.apiClient = apiClient;
    }

    @Override
    public WeatherInfo getWeather(final User user) {
        return getWeatherBitWeather(user, WEATHER)
                .map(w -> (WeatherInfo) w)
                .orElse(() -> NO_INFO);
    }

    @Override
    public ForecastInfo getForecast(final User user) {
        return () -> List.of(NO_INFO);
    }

    @Override
    public ForecastSource getSource() {
        return WEATHERBIT;
    }

    public Optional<WeatherBitWeather> getWeatherBitWeather(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSource(mapToForecastLocationEntity(forecastLocation),
                        WEATHERBIT.name(),
                        SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
        }
        if (!apiCallCounterService.canApiCallBePerformed(WEATHERBIT)) {
            log.info("Api call limit reached");
            return Optional.empty();
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        Optional<String> jsonResponse = apiClient.getCurrentWeather(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());

        jsonResponse.ifPresent(json -> apiCallCounterService.incrementApiCallCounter(WEATHERBIT));
        Optional<ForecastEntity> entity = jsonResponse.map(json -> new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                json,
                WEATHERBIT.name(),
                mapToForecastLocationEntity(forecastLocation)));
        entity.ifPresent(repo::save);

        return entity.map(WeatherBitMapper::readForecast);
    }
}
