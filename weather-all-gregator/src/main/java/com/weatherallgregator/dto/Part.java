package com.weatherallgregator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Part(String partName,
                   int tempMin,
                   int tempAvg,
                   int tempMax,
                   int windSpeed,
                   int windGust,
                   String windDir,
                   int pressureMm,
                   int pressurePa,
                   int humidity,
                   int precMm,
                   int precProb,
                   int precPeriod,
                   String icon,
                   String condition,
                   int feelsLike,
                   String daytime,
                   boolean polar) {
}
