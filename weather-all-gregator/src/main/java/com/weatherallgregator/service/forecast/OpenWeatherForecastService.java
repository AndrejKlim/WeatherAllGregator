package com.weatherallgregator.service.forecast;

import com.weatherallgregator.client.OpenWeatherApiClient;
import com.weatherallgregator.dto.*;
import com.weatherallgregator.dto.jdbc.DatePressureRaw;
import com.weatherallgregator.dto.openweather.OpenWeatherForecast;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.enums.ForecastTypeJpa;
import com.weatherallgregator.jpa.entity.ForecastEntity;
import com.weatherallgregator.jpa.repo.ForecastRepo;
import com.weatherallgregator.mapper.OpenWeatherMapper;
import com.weatherallgregator.service.ApiCallCounterService;
import com.weatherallgregator.util.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;

import static com.weatherallgregator.enums.ForecastSource.OPEN_WEATHER;
import static com.weatherallgregator.enums.ForecastType.FORECAST;
import static com.weatherallgregator.enums.ForecastType.WEATHER;
import static com.weatherallgregator.enums.ForecastTypeJpa.MIXED;
import static com.weatherallgregator.mapper.ForecastLocationMapper.mapToForecastLocationEntity;
import static com.weatherallgregator.mapper.OpenWeatherMapper.readForecast;

@Component
@Slf4j
public class OpenWeatherForecastService extends ForecastService{

    private final OpenWeatherApiClient apiClient;
    private final JdbcTemplate jdbcTemplate;

    public OpenWeatherForecastService(final ForecastRepo repo,
                                      final ApiCallCounterService apiCallCounterService,
                                      final OpenWeatherApiClient apiClient,
                                      final JdbcTemplate jdbcTemplate) {
        super(repo, apiCallCounterService);
        this.apiClient = apiClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WeatherInfo getWeather(final User user) {
        return getOpenWeatherForecast(user, WEATHER)
                .map(WeatherInfo.class::cast)
                .orElse(NO_WEATHER_INFO);
    }

    @Override
    public ForecastInfo getForecast(final User user) {
        return getOpenWeatherForecast(user, FORECAST)
                .map(ForecastInfo.class::cast)
                .orElse(NO_FORECAST_INFO);
    }

    public List<DatePressure> getPressures() {
        var datePressureMap = new HashMap<Long, List<DatePressureRaw>>();
        for (DatePressureRaw dp : getPressuresJdbc()) {
            // group dates by inner timestamp
            datePressureMap.computeIfAbsent(dp.getTimestamp(), l -> new ArrayList<>()).add(dp);
        }
        return datePressureMap.values().stream()
                // database can return multiple pressures per one date,pass latest
                .map(datePressures -> datePressures.stream().max(Comparator.comparingLong(DatePressureRaw::getCreatedAt)).orElse(null))
                .filter(Objects::nonNull)
                .map(dpRaw -> new DatePressure(Instant.ofEpochMilli(dpRaw.getTimestamp() * 1000).atZone(ZoneId.systemDefault()).toLocalDate(), ConvertUtils.hPaToMm(dpRaw.getPressure())))
                .sorted(Comparator.comparing(DatePressure::getDate))
                .toList();
    }

    @Override
    public ForecastSource getSource() {
        return OPEN_WEATHER;
    }

    public Optional<OpenWeatherForecast> getOpenWeatherForecast(final User user, final ForecastType forecastType) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<ForecastEntity> lastForecastByLocation =
                repo.findFirstByForecastLocationAndSourceAndType(mapToForecastLocationEntity(forecastLocation),
                        OPEN_WEATHER.name(), MIXED.name(), SORT_DESC_BY_CREATED_AT);

        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
        }
        if (!apiCallCounterService.canApiCallBePerformed(OPEN_WEATHER)) {
            log.info("Api call limit reached");
            return Optional.empty();
        }

        log.info("Getting new forecast from api, saving and returning to bot");
        Optional<String> jsonResponse = apiClient.getCurrentWeather(forecastLocation.getLat().toString(),
                forecastLocation.getLon().toString());

        jsonResponse.ifPresent(s -> apiCallCounterService.incrementApiCallCounter(OPEN_WEATHER));

        Optional<ForecastEntity> entity = jsonResponse
                .map(json -> new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), json,
                        OPEN_WEATHER.name(), MIXED.name(), mapToForecastLocationEntity(forecastLocation)));
        entity.ifPresent(repo::save);

        return entity.map(OpenWeatherMapper::readForecast);
    }

    private List<DatePressureRaw> getPressuresJdbc() {
        return jdbcTemplate.query("SELECT created_at, (jsonb_array_elements(forecast::jsonb -> 'daily') ->> 'dt')::bigint as timestamp, jsonb_array_elements(forecast::jsonb -> 'daily') ->> 'pressure' as pressure\n" +
                "from forecast\n" +
                "where forecast.source = 'OPEN_WEATHER' and created_at >= extract(epoch  from (now() - interval '5 day'))\n" +
                "order by timestamp desc;", (rs, rowNum) -> new DatePressureRaw(rs.getLong("created_at"), rs.getLong("timestamp"), rs.getInt("pressure")));
    }
}
