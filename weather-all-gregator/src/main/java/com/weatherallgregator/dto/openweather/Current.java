package com.weatherallgregator.dto.openweather;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Current {

    long dt;
    long sunrise;
    long sunset;

    float temp;
    float feelsLike;
    int pressure; // in hPa
    int humidity;
    float dewPoint;
    int clouds;
    float uvi;
    int visibility;

    int windSpeed;
    int windDeg;
    int windGust;

    List<Weather> weather;
    Rain rain;
    Snow snow;
}
