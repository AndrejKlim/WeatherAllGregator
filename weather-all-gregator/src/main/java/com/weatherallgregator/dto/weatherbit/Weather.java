package com.weatherallgregator.dto.weatherbit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Weather {

    String icon;
    String code;
    String description;
}
