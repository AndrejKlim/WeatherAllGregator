package com.weatherallgregator.dto.yandex;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Fact {

    Long obsTime;
    int temp;
    int feelsLike;
    String icon;
    String condition;
    int windSpeed;
    String windDir;
    int pressureMm;
    int pressurePa;
    int humidity;
    String daytime;
    boolean polar;
    String season;
    float windGust;
}
