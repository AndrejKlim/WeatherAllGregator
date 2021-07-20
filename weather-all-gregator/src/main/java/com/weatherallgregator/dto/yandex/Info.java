package com.weatherallgregator.dto.yandex;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Info {

    String url;
    float lat;
    float lon;
}
