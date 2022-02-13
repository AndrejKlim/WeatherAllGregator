package com.weatherallgregator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Component
@Slf4j
public class WeatherBitApiClient extends ApiClient{

    @Value("${app.forecast.weatherbit.url.current}")
    private String currentWeatherBitApiUrl;
    @Value("${app.forecast.weatherbit.url.daily}")
    private String dailyForecastWeatherBitApiUrl;
    @Value("${app.forecast.weatherbit.api-key}")
    private String apiKey;

    public WeatherBitApiClient(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<String> getCurrentWeather(final String latitude, final String longitude) {
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return Optional.empty();
        }

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("key", apiKey);

        var httpEntity = HttpEntity.EMPTY;

        return Optional.ofNullable(sendGet(currentWeatherBitApiUrl, httpEntity, params));
    }

    public Optional<String> getDailyForecast(final String latitude, final String longitude) {
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return Optional.empty();
        }

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("key", apiKey);

        var httpEntity = HttpEntity.EMPTY;

        return Optional.ofNullable(sendGet(dailyForecastWeatherBitApiUrl, httpEntity, params));
    }
}
