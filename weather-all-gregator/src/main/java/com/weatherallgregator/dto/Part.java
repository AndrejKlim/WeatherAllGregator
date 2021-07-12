package com.weatherallgregator.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Part {

    String partName;
    int tempMin;
    int tempAvg;
    int tempMax;
    float windSpeed;
    float windGust;
    String windDir;
    int pressureMm;
    int pressurePa;
    int humidity;
    int precMm;
    int precProb;
    int precPeriod;
    String icon;
    String condition;
    int feelsLike;
    String daytime;
    boolean polar;
}
