package com.weatherallgregator.dto.openweather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeelsLike {

    float morn;
    float day;
    float eve;
    float night;
}
