package com.weatherallgregator.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherApiClient {

    private final RestTemplate restTemplate;

    @Value("${app.forecast.openweather.url}")
    private String openWeatherApiUrl;
    @Value("${app.forecast.openweather.api-key}")
    private String apiKey;

    public String getCurrentWeather(final String latitude, final String longitude) {
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return null; // TODO can throw exception or return Optional.empty()
        }

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", apiKey);

        var httpEntity = HttpEntity.EMPTY;

        log.info("Request entity = {}\n params = {}", httpEntity, params);
        return restTemplate.exchange(openWeatherApiUrl, HttpMethod.GET, httpEntity, String.class, params).getBody();
    }
}
