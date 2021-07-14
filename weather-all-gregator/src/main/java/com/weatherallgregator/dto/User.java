package com.weatherallgregator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    private Long id;
    private String timeZone;
    private ForecastLocation forecastLocation;
}
