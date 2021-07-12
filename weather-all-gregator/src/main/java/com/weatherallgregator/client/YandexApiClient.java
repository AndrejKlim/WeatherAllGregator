package com.weatherallgregator.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class YandexApiClient {

    private static final String YANDEX_API_KEY_HEADER = "X-Yandex-API-Key";
    private static final String LANG = "ru_RU"; // TODO can be passed in method params

    @Value("${app.forecast.yandex.url}")
    private String yandexApiUrl;
    @Value("${app.forecast.yandex.api-key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    public String getForecast(final String latitude, final String longitude){
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return null; // TODO can throw exception or return Optional.empty()
        }

        var headers = new HttpHeaders();
        headers.set(YANDEX_API_KEY_HEADER, apiKey);

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("lang", LANG);

        var httpEntity = new HttpEntity<>(headers);

        log.info("Request entity = {}\n params = {}", httpEntity, params);
        return restTemplate.exchange(yandexApiUrl, HttpMethod.GET, httpEntity, String.class, params).getBody();
    }
}
