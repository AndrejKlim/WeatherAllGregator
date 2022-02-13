package com.weatherallgregator.service.forecast;

import com.weatherallgregator.client.WeatherBitApiClient;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.ForecastLocation;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.enums.ForecastTypeJpa;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import com.weatherallgregator.mapper.WeatherBitMapper;
import com.weatherallgregator.service.ApiCallCounterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.weatherallgregator.enums.ForecastSource.WEATHERBIT;
import static com.weatherallgregator.enums.ForecastType.FORECAST;
import static com.weatherallgregator.enums.ForecastType.WEATHER;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.WeatherBitMapper.readForecast;
import static com.weatherallgregator.mapper.WeatherBitMapper.readWeather;

@Component
@Slf4j
public class WeatherBitService extends ForecastService {

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
                .orElse(NO_WEATHER_INFO);
    }

    @Override
    public ForecastInfo getForecast(final User user) {
        return getDailyForecast(user, FORECAST)
                .orElse(NO_FORECAST_INFO);
    }

    @Override
    public ForecastSource getSource() {
        return WEATHERBIT;
    }

    public Optional<WeatherInfo> getWeatherBitWeather(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSourceAndType(mapToForecastLocationEntity(forecastLocation),
                        WEATHERBIT.name(), ForecastTypeJpa.WEATHER.name(), SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readWeather(lastForecastByLocation.get()));
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
                ForecastTypeJpa.WEATHER.name(),
                mapToForecastLocationEntity(forecastLocation)));
        entity.ifPresent(repo::save);

        return entity.map(WeatherBitMapper::readWeather);
    }

    public Optional<ForecastInfo> getDailyForecast(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSourceAndType(mapToForecastLocationEntity(forecastLocation),
                        WEATHERBIT.name(), FORECAST.name(), SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
        }
        if (!apiCallCounterService.canApiCallBePerformed(WEATHERBIT)) {
            log.info("Api call limit reached");
            return Optional.empty();
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        Optional<String> jsonResponse = apiClient.getDailyForecast(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());

        jsonResponse.ifPresent(json -> apiCallCounterService.incrementApiCallCounter(WEATHERBIT));
        Optional<ForecastEntity> entity = jsonResponse.map(json -> new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                json,
                WEATHERBIT.name(),
                ForecastTypeJpa.FORECAST.name(),
                mapToForecastLocationEntity(forecastLocation)));
        entity.ifPresent(repo::save);

        return entity.map(WeatherBitMapper::readForecast);
    }
}
