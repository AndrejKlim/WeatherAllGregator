package com.weatherallgregator.dto.openweather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sys {

    int type;
    long id;
    String country;
    long sunrise;
    long sunset;
}
