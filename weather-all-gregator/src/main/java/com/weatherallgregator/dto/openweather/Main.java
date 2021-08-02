package com.weatherallgregator.dto.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Main {
    
    float temp;
    float feelsLike;
    float tempMin;
    float tempMax;
    int pressure; // in hPa
    int humidity;
    @JsonProperty("sea_level")
    int seaLevel;
    @JsonProperty("grnd_level")
    int groundLevel;
}
