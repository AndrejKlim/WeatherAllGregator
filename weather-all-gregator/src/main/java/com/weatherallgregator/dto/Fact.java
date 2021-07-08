package com.weatherallgregator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Fact(Long obsTime,
                   int temp,
                   int feelsLike,
                   String icon,
                   String condition,
                   int windSpeed,
                   String windDir,
                   int pressureMm,
                   int pressurePa,
                   int humidity,
                   String daytime,
                   boolean polar,
                   String season,
                   int windGust) {
}
