package com.weatherallgregator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record YandexForecast(Long now,
                             String nowDt,
                             Info info,
                             Fact fact,
                             Forecast forecast) {
}
