package com.weatherallgregator.model;

import com.weatherallgregator.enums.Condition;
import com.weatherallgregator.enums.DayTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YandexPartModel {

    DayTime dayTime;
    int averageTemp;
    Condition condition;
    float windSpeed;
    int pressureMm;
    int humidity;
    int precipitationMm;
    int precipitationProbability; // rain or snow
}
