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
public class OpenWeatherApiClient extends ApiClient{
    @Value("${app.forecast.openweather.url}")
    private String openWeatherApiUrl;
    @Value("${app.forecast.openweather.api-key}")
    private String apiKey;

    public OpenWeatherApiClient(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<String> getCurrentWeather(final String latitude, final String longitude) {
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return Optional.empty();
        }

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", apiKey);

        var httpEntity = HttpEntity.EMPTY;

        return Optional.ofNullable(sendGet(openWeatherApiUrl, httpEntity, params));
    }
}
