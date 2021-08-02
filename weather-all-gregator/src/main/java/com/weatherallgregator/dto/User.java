package com.weatherallgregator.dto;

import com.weatherallgregator.enums.ForecastSource;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    private Long id;
    private String timeZone;
    private ForecastLocation forecastLocation;
    private List<ForecastSource> forecastSourceList;
}
