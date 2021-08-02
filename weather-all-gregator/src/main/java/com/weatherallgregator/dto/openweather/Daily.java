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
public class Daily {

    long dt;
    long sunrise;
    long sunset;
    long moonrise;
    long moonset;
    float moonPhase;

    Temp temp;
    FeelsLike feelsLike;
    int pressure; // in hPa
    int humidity;
    float dewPoint;
    int clouds;
    float uvi;
    int pop;

    int windSpeed;
    int windDeg;
    int windGust;

    List<Weather> weather;
    float rain;
    float snow;
}
