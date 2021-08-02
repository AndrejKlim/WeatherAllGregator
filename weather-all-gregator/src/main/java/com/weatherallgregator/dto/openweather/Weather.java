package com.weatherallgregator.dto.openweather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Weather {

    long id;
    String main;
    String description;
    String icon;
}
