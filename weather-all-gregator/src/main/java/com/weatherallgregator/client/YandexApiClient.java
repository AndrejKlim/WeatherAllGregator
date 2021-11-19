package com.weatherallgregator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Component
@Slf4j
public class YandexApiClient extends ApiClient{

    private static final String YANDEX_API_KEY_HEADER = "X-Yandex-API-Key";
    private static final String LANG = "ru_RU";

    @Value("${app.forecast.yandex.url}")
    private String yandexApiUrl;
    @Value("${app.forecast.yandex.api-key}")
    private String apiKey;

    public YandexApiClient(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Optional<String> getForecast(final String latitude, final String longitude){
        if (!StringUtils.hasText(latitude) || !StringUtils.hasText(longitude)) {
            return Optional.empty();
        }

        var headers = new HttpHeaders();
        headers.set(YANDEX_API_KEY_HEADER, apiKey);

        var params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("lang", LANG);

        var httpEntity = new HttpEntity<>(headers);

        log.info("Request entity = {}\n params = {}", httpEntity, params);

        return Optional.ofNullable(sendGet(yandexApiUrl, httpEntity, params));
    }
}
