package com.weatherallgregator.dto.weatherbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherBitDailyData {

    long ts;
    float temp;
    @JsonProperty("high_temp")
    float highTemp;
    @JsonProperty("low_temp")
    float lowTemp;
    float pop;
    int precip;
    int pres;
    @JsonProperty("wind_spd")
    float windSpeed;
    int rh;
    Weather weather;
}
