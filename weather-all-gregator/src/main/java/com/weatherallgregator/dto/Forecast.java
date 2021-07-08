package com.weatherallgregator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Forecast(LocalDate date,
                       Long dateTs,
                       int week,
                       String sunrise,
                       String sunset,
                       int moonCode,
                       String moonText,
                       List<Part> parts) {
}
