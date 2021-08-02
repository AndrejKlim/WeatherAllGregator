package com.weatherallgregator.dto.openweather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Minutely {

    long dt;
    float precipitation;
}
