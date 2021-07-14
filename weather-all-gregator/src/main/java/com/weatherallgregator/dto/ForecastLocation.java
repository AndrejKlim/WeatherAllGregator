package com.weatherallgregator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForecastLocation {

    private Long id;
    private Float lat;
    private Float lon;
}
